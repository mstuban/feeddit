(function() {
    'use strict';

    angular
        .module('feedditApp')
        .controller('PostDetailController', PostDetailController);

    PostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Post'];

    function PostDetailController($scope, $rootScope, $stateParams, previousState, entity, Post) {
        var vm = this;

        vm.authorID =
        vm.post = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('feedditApp:postUpdate', function(event, result) {
            vm.post = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
