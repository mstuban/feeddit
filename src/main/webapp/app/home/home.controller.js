(function() {
    'use strict';

    angular
        .module('feedditApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['PostAdmin', '$scope', '$stateParams', 'Principal', 'LoginService', '$state'];

    function HomeController (PostAdmin, $scope, $stateParams, Principal, LoginService, $state) {
        var vm = this;

        $scope.loggedOut = $stateParams.loggedOut;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.posts = [];
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
            loadAll();
        });

        loadAll();

        function loadAll() {
            PostAdmin.query(function(result) {
                vm.posts = result;
                vm.searchQuery = null;
            });
        }

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }

        function login() {
            collapseNavbar();
            LoginService.open();
        }

    }
})();
