var express = require('express');
var router = express.Router();

var path = require('path');
var CryptoJS = require("crypto-js");
var crypto = require('crypto');
var config = require(path.join(__dirname + "/../" + "config/config"));

/* GET data page. */
router.get('/', function(req, res, next) {
    var conn = req.app.locals.connection;

    conn.query("select glu, temp, hrt, bp from scdc_m3 where id=(select max(id) from scdc_m3) limit 1;", function(err, rows, fields){
        var response = {};
        if(err) {
            // If there were errors in the query process
            response.msg = 500;

            response.errors = {};
            response.errors.msg = "SQL Error";

            response.ret = '';
        } else {
            if(rows.length == 0) {
                response.msg = 500;
                response.errors = {};
                response.errors.msg = "No Data Recorded yet";
                response.ret = '';
            } else {
                // Response is success
                response.msg = 200;

                response.errors = {};

                response.ret = {};
                var data = rows[0];
                var str_data = JSON.stringify(data);
                console.log(str_data);
                var encData = encryptString(str_data);
                response.ret = encData;
            }
        }

        res.send(JSON.stringify(response));
        console.log("\nResponse was\n");
        console.log(response);
        console.log("\n");
    });
});

/**
* A function to encrypt the response
* @param stringToEncrypt is the string to encrypt :-P
* @return the encrypted response to be sent to the API
*/
function encryptString(stringToEncrypt) {
    var key = config.encKey;
    var cipher = crypto.createCipher('aes-128-ecb',key);
    var crypted = cipher.update(stringToEncrypt,'utf-8','hex');
    crypted += cipher.final('hex');
    return crypted;
}

module.exports = router;
