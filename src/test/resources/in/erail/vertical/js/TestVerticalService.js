/*/in/erail/vertical/js/TestVerticalService.js*/
/* global vertx */

vertx.setPeriodic(1000, function (id) {
  vertx.eventBus().send("vertical.test.js", "success");
  console.log("timer fired!");
});