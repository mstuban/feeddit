(function () {
    'use strict';

    angular
        .module('feedditApp')
        .controller('PostController', PostController);

    PostController.$inject = ['Post', 'PostAdmin', '$state', 'Auth', 'Principal'];

    function PostController(Post, PostAdmin, $state, Auth, Principal) {
        var vm = this;
        vm.account = null;
        vm.userID = null;


        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.userID = vm.account.id;
                vm.isAuthenticated = Principal.isAuthenticated;

                    if (account.authorities.indexOf("ROLE_ADMIN") >= 0) {
                        loadAllForAdmin();
                    }
                    else{
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
    }
})();
