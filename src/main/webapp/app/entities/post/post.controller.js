(function () {
    'use strict';

    angular
        .module('feedditApp')
        .controller('PostController', PostController);

    PostController.$inject = ['$http', 'Post', 'PostAdmin', '$state', 'Auth', 'Principal'];

    function PostController($http, Post, PostAdmin, $state, Auth, Principal) {
        var vm = this;
        vm.account = null;
        vm.userID = null;
        vm.idArray = [];
        vm.isAnyCheckboxSelected = false;
        vm.showNoPostsMessage = false;

        vm.addToIdArray = function (id) {
            console.log("addtoIdArray");

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

            console.log(vm.idArray);

        };

        vm.deletePostsByIdArray = function () {
            var areYouSure = confirm("Are you sure?");
            if (areYouSure) {
                $http.delete("/api/currentUser/posts/" + vm.idArray)
                    .success(function () {
                        loadAll();
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
                if (vm.posts.length === 0) {
                    vm.showNoPostsMessage = true;
                }
                vm.searchQuery = null;
            });
        }

        function loadAllForAdmin() {
            PostAdmin.query(function (result) {
                vm.posts = result;
                if (vm.posts.length === 0) {
                    vm.showNoPostsMessage = true;
                }
                vm.searchQuery = null;
            });
        }
    }
})();
