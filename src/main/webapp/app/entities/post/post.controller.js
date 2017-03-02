(function () {
    'use strict';

    angular
        .module('feedditApp')
        .controller('PostController', PostController);

    PostController.$inject = ['$http', '$scope', 'Post', 'PostAdmin', '$state', 'Principal', 'AlertService'];

    function PostController($http, $scope, Post, PostAdmin, $state, Principal, AlertService) {
        var vm = this;
        $scope.sort = function (keyname) {
            $scope.sortKey = keyname;
            $scope.reverse = !$scope.reverse;
        };
        vm.account = null;
        vm.userID = null;
        vm.idArray = [];
        vm.isAnyCheckboxSelected = false;
        $scope.items = 5;
        $scope.toggle = true;

        $scope.$watch('toggle', function () {
            $scope.toggle ? (vm.deselectAllCheckboxes(), $scope.selectAllButtonText = "Select all") : (vm.selectAllCheckboxes(), $scope.selectAllButtonText = "Deselect all");
        });

        loadAll();

        vm.selectAllCheckboxes = function () {
            var inputs = document.querySelectorAll("input[type='checkbox']");

            angular.forEach(vm.posts, function (post, i) {
                inputs[i].checked = true;
                vm.idArray.push(post.id);
            });

            vm.isAnyCheckboxSelected = true;
        };

        vm.deselectAllCheckboxes = function () {
            var inputs = document.querySelectorAll("input[type='checkbox']");

            angular.forEach(inputs, function (input, i) {
                input.checked = false;
            });
            vm.idArray = [];
            vm.isAnyCheckboxSelected = false;
        };

        vm.addToIdArray = function (id) {
            var inputs = document.querySelectorAll("input[type='checkbox']");
            var areAllChecked = false;

            angular.forEach(inputs, function (input) {
                if (input.checked) {
                    areAllChecked = true;
                } else {
                    areAllChecked = false;
                }
            });

            if(areAllChecked){
                $scope.selectAllButtonText = "Deselect all"
                $scope.toggle = vm.deselectAllCheckboxes();
            }

            if (vm.idArray.indexOf(id) == -1) {
                vm.idArray.push(id);
            }
            else {
                var index = vm.idArray.indexOf(id);
                vm.idArray.splice(index, 1);
            }

            var inputs = document.querySelectorAll("input[type='checkbox']");
            if (vm.idArray.length === 0) {
                vm.isAnyCheckboxSelected = false;
            }
            for (var i = 0; i < inputs.length; i++) {
                if (inputs[i].checked) {
                    vm.isAnyCheckboxSelected = true;
                }
            }

        };

        vm.deletePostsByIdArray = function () {
            var areYouSure = confirm("Are you sure?");
            if (areYouSure) {
                $http.delete("/api/currentUser/posts/" + vm.idArray)
                    .success(function () {
                        if (vm.account.authorities.indexOf("ROLE_ADMIN") >= 0) {
                            loadAllForAdmin();
                        }
                        else {
                            loadAll();
                        }

                        AlertService.success("Successfully deleted " + vm.idArray.length + " posts!");

                    })
                    .error(function (status, header) {
                    });
            }
        };

        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
                vm.userID = vm.account.id;
                vm.isAuthenticated = Principal.isAuthenticated;

                if (account.authorities.indexOf("ROLE_ADMIN") >= 0) {
                    loadAllForAdmin();
                }
                else {
                    loadAll();
                }

                if (vm.isAuthenticated) {
                } else {
                    $state.go('home');
                }
            });
        }

        vm.posts = [];

        function loadAll() {
            Post.query(function (result) {
                vm.posts = result;
                vm.searchQuery = null;
            });
        }

        function loadAllForAdmin() {
            PostAdmin.query(function (result) {
                vm.posts = result;
                vm.searchQuery = null;
            });
        }

        $scope.sortKey = 'numberOfUpvotes';
        $scope.reverse = true;
    }
})();
