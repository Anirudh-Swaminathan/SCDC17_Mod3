var express = require('express');
var router = express.Router();

/* GET data page. */
router.get('/', function(req, res, next) {
    var conn = req.app.locals.connection;

    conn.query("select glu, temp, hrt, bp from scdc_m3 where id=(select max(id) from scdc_m3) limit 1;", function(err, rows, fields){
        var response = {};
        if(err) {
            response.msg = 500;

            response.errors = {};
            response.errors.msg = "SQL Error";

            response.ret = {};
        } else {
            response.msg = 200;

            response.errors = {};

            response.ret = {};
            var data = rows[0];
            response.ret = data;
        }

        res.send(JSON.stringify(response));
        console.log("\nResponse was\n");
        console.log(response);
        console.log("\n");
    });
});

module.exports = router;
