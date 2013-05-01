package assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import fi.iki.elonen.ServerRunner;
import fi.iki.elonen.SimpleWebServer;

public class ThreadPoolServer extends SimpleWebServer {
	/**
	 * The distribution licence
	 */
	private static final String LICENCE = "Copyright (C) 2001,2005-2011 by Jarno Elonen <elonen@iki.fi>\n"
			+ "and Copyright (C) 2010 by Konstantinos Togias <info@ktogias.gr>\n"
			+ "\n"
			+ "Redistribution and use in source and binary forms, with or without\n"
			+ "modification, are permitted provided that the following conditions\n"
			+ "are met:\n"
			+ "\n"
			+ "Redistributions of source code must retain the above copyright notice,\n"
			+ "this list of conditions and the following disclaimer. Redistributions in\n"
			+ "binary form must reproduce the above copyright notice, this list of\n"
			+ "conditions and the following disclaimer in the documentation and/or other\n"
			+ "materials provided with the distribution. The name of the author may not\n"
			+ "be used to endorse or promote products derived from this software without\n"
			+ "specific prior written permission. \n"
			+ " \n"
			+ "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n"
			+ "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n"
			+ "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n"
			+ "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
			+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n"
			+ "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n"
			+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n"
			+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
			+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n"
			+ "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";

	private ThreadPoolAsyncRunner asyncRunner;

	public ThreadPoolServer(String host, int port, File wwwroot, int nbThreads) {
		super(host, port, wwwroot);
		asyncRunner = new ThreadPoolAsyncRunner(nbThreads);
		setAsyncRunner(asyncRunner);
	}

	@Override
	public Response serve(String uri, Method method,
			Map<String, String> header, Map<String, String> parms,
			Map<String, String> files) {
		Response rSuper = super.serveFile(uri, header, getRootDir());
		Response result = null;
		if (rSuper.mimeType.contains("text/htm")) {
			result = new Response(rSuper.status, rSuper.mimeType,
					manipulateResponse(rSuper.data));
		} else {
			result = rSuper;
		}
		return result;
	}

	private String manipulateResponse(InputStream inputStream) {
		InputStreamReader is = new InputStreamReader(inputStream);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(is);

		try {
			String read = br.readLine();
			while (read != null) {
				read = read.replace("<body>", "<body>\nServed by thread #"
						+ Thread.currentThread().getId() + "<br/>");
				sb.append(read);
				read = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	@Override
	public void stop() {
		super.stop();
		asyncRunner.shutdownExecutor();
	}

	private class ThreadPoolAsyncRunner implements AsyncRunner {
		private int nThreads;
		private ThreadPoolExecutor executor;

		protected ThreadPoolAsyncRunner(int nbTh) {
			nThreads = nbTh;
			executor = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(nThreads);
		}

		@Override
		public void exec(Runnable code) {
			executor.execute(code);
		}

		protected void shutdownExecutor() {
			executor.shutdown();
			while (!executor.isTerminated()) {

			}
		}
	}

	public static void main(String[] args) {

		// Defaults
		int port = 8080;
		String host = "127.0.0.1";
		File wwwroot = new File("src/webapp").getAbsoluteFile();
		int nbThreads = 10;

		// Show licence if requested
		for (int i = 0; i < args.length; ++i)
			if (args[i].equalsIgnoreCase("-h"))
				host = args[i + 1];
			else if (args[i].equalsIgnoreCase("-p"))
				port = Integer.parseInt(args[i + 1]);
			else if (args[i].equalsIgnoreCase("-d"))
				wwwroot = new File(args[i + 1]).getAbsoluteFile();
			else if (args[i].equalsIgnoreCase("-t"))
				nbThreads = Integer.parseInt(args[i + 1]);
			else if (args[i].toLowerCase().endsWith("licence")) {
				System.out.println(LICENCE + "\n");
				break;
			}

		ServerRunner.executeInstance(new ThreadPoolServer(host, port, wwwroot,
				nbThreads));
	}

}
