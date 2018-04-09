/*/in/erail/vertical/js/TestVerticalService.js*/
/* global vertx */
var glue = Java.type("in.erail.glue.Glue").instance();

vertx.setPeriodic(1000, function (id) {
    var service = glue.resolve("/io/vertx/core/VerticalDeployer");
    vertx.eventBus().send("vertical.test.js", service.getVerticalNames()[0]);
    console.log("timer fired! " + service.getVerticalNames()[0]);
});
