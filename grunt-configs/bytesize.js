module.exports = function (grunt, options) {
    return {
        js: {
            src: [
                options.staticTargetDir + 'javascripts/app.js',
                options.staticTargetDir + 'javascripts/bootstraps/*.js'
            ]
        }
    };
};
