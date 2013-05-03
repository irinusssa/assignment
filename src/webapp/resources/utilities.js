$(window)
		.load(
				function() {
					/* prevent default click handle */
					$('input[type=button]').draggable({cancel:false});
					$('textarea').draggable({cancel:false});
					/* ---- */
					$('.draggable').draggable({
						'revert' : 'invalid'
					});
					$('#frame')
							.contents()
							.find('.droppable')
							.droppable(
									{
										drop : function(event, ui) {
											$
													.post(
															'resources/assignmentAdobeB_response.txt',
															function(data) {
																alert('DOM with tag '
																		+ event.originalEvent.target.tagName
																		+ ' was dropped. Response was : '
																		+ data);
															});
										}
									});

					/* fix small bug for iFrames, reference http://stackoverflow.com/questions/6817758/drag-and-drop-elements-into-an-iframe-droppable-area-has-wrong-coordinates-and */
					jQuery.ui.ddmanager.prepareOffsets = function(t, event) {

						var m = $.ui.ddmanager.droppables[t.options.scope]
								|| [];
						var type = event ? event.type : null;
						var list = (t.currentItem || t.element).find(
								":data(droppable)").andSelf();

						droppablesLoop: for ( var i = 0; i < m.length; i++) {

							if (m[i].options.disabled
									|| (t && !m[i].accept.call(m[i].element[0],
											(t.currentItem || t.element))))
								continue;
							for ( var j = 0; j < list.length; j++) {
								if (list[j] == m[i].element[0]) {
									m[i].proportions.height = 0;
									continue droppablesLoop;
								}
							}
							;
							m[i].visible = m[i].element.css("display") != "none";
							if (!m[i].visible)
								continue;

							m[i].offset = m[i].element.offset();

							m[i].offset.top += $('#frame').offset().top;
							m[i].offset.left += $('#frame').offset().left;

							m[i].proportions = {
								width : m[i].element[0].offsetWidth,
								height : m[i].element[0].offsetHeight
							};

						}

					}

				});