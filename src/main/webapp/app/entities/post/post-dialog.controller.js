(function () {
    'use strict';

    angular
        .module('feedditApp')
        .controller('PostDialogController', PostDialogController);

    PostDialogController.$inject = ['$timeout', '$scope', '$state', 'Auth', '$stateParams', 'Principal', '$uibModalInstance', 'entity', 'Post'];

    function PostDialogController($timeout, $scope, $state, Auth, $stateParams, Principal, $uibModalInstance, entity, Post) {
        var vm = this;

        vm.post = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.authorID = null;
        vm.submitDate = new Date();
        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
                vm.byUser = vm.account.login;
                vm.authorID = account.id;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            vm.post.submitDate = vm.submitDate;
            vm.post.authorID = vm.authorID;
            vm.post.numberOfUpvotes = 0;
            if (vm.post.id !== null) {
                Post.update(vm.post, onSaveSuccess, onSaveError);
            } else {
                Post.save(vm.post, onSaveSuccess, onSaveError);
            }
        }

        function updateUpvotes() {
            vm.isSaving = true;

            vm.post.numberOfUpvotes = vm.numberOfUpvotes;

            if (vm.post.id !== null) {
                Post.update(vm.post, onSaveSuccess, onSaveError);
            } else {
                Post.save(vm.post, onSaveSuccess, onSaveError);
            }
        }


        function onSaveSuccess(result) {
            $scope.$emit('feedditApp:postUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.submitDate = false;

        function openCalendar(date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
