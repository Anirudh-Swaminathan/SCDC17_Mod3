var express = require('express');
var router = express.Router();

var path = require('path');
var CryptoJS = require("crypto-js");
var config = require(path.join(__dirname + "/../" + "config/config"));

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
            var str_data = JSON.stringify(data);
            console.log(str_data);
            var encData = encryptString(str_data);
            response.ret = encData;
        }

        res.send(JSON.stringify(response));
        console.log("\nResponse was\n");
        console.log(response);
        console.log("\n");
    });
});

function encryptString(stringToEncrypt) {
    encK = config.encKey;
    encI = config.encIv;

    var rkEncryptionKey = CryptoJS.enc.Base64.parse(encK);
    var rkEncryptionIv = CryptoJS.enc.Base64.parse(encI);

    var utf8Stringified = CryptoJS.enc.Utf8.parse(stringToEncrypt);
    var encrypted = CryptoJS.AES.encrypt(utf8Stringified.toString(), rkEncryptionKey, {mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: rkEncryptionIv});
    return encrypted.ciphertext.toString(CryptoJS.enc.Base64);
}

module.exports = router;
