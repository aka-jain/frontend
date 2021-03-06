define([
    'common/utils/config',
    'common/utils/detect'
], function (
    config,
    detect
) {
    return function () {

        this.id = 'LiveBlogChromeNotificationsProd2';
        this.start = '2016-07-15';
        this.expiry = '2016-09-30';
        this.author = 'Francis Carr';
        this.description = 'Allows users to to subscribe to live blogs on chrome - separately to internal test so we can run the internal one on prod if need be';
        this.audience = 0.1;
        this.audienceOffset = 0.0;
        this.successMeasure = '';
        this.showForSensitive = true;
        this.audienceCriteria = 'Chrome users on desktop and android';
        this.dataLinkNames = '';
        this.idealOutcome = '';

        this.canRun = function () {
            return detect.getUserAgent.browser === 'Chrome' && config.page.contentType === 'LiveBlog' && !detect.isIOS();
        };

        this.variants = [
            {
                id: 'control',
                test: function () {}
            },
            {
                id: 'show-notifications',
                test: function () {}
            }
        ];
    };
});
