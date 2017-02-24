/**
 * Created by marko on 23.02.17..
 */
(function () {
    'use strict';
    angular
        .module('feedditApp')
        .factory('PostAdmin', PostAdmin);

    PostAdmin.$inject = ['$resource', 'DateUtils'];

    function PostAdmin($resource, DateUtils) {

        var resourceUrl = 'api/posts/:id';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.submitDate = DateUtils.convertLocalDateFromServer(data.submitDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.submitDate = DateUtils.convertLocalDateToServer(copy.submitDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.submitDate = DateUtils.convertLocalDateToServer(copy.submitDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
