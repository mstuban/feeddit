(function () {
    'use strict';

    angular
        .module('feedditApp', [
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar',
            'angularUtils.directives.dirPagination'
        ])
        .config(function(paginationTemplateProvider) {
            paginationTemplateProvider.setString('<div class="text-center"><a href="" title="Previous page" ng-class="{ disabled : pagination.current == 1 }" ng-click="setCurrent(pagination.current - 1)"><span class="glyphicon glyphicon-chevron-left"></span></a> <span>Page {{pagination.current}}/{{pagination.last}}</span> <a href="" title="Next page" ng-class="{ disabled : pagination.current == pagination.last }" ng-click="setCurrent(pagination.current + 1)"><span class="glyphicon glyphicon-chevron-right"></a></div>');
        })
        .run(run);

    run.$inject = ['stateHandler', 'translationHandler'];

    function run(stateHandler, translationHandler) {
        stateHandler.initialize();
        translationHandler.initialize();
    }
})();
