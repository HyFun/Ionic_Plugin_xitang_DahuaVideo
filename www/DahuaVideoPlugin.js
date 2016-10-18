var exec = require('cordova/exec');

exports.startDahuaVideo = function(userId, userName, userType) {
    console.log('执行到了插件方法');
    exec(null, null, "DahuaVideoPlugin", "startDahuaVideo", [userId,userName,userType]);
};

// module.exports = {
//     startDahuaVideo : function (content) {
//       exec(null, null, "DahuaVideoPlugin", "startDahuaVideo", [content]);
//     }
// }
