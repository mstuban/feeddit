(function () {
    'use strict';

    angular
        .module('feedditApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['PostAdmin', '$scope', '$stateParams', 'Principal', 'LoginService', '$state', '$http'];

    function HomeController(PostAdmin, $scope, $stateParams, Principal, LoginService, $state, $http) {
        var vm = this;
        $scope.loggedOut = $stateParams.loggedOut;
        $scope.sort = function (keyname) {
            $scope.sortKey = keyname;   //set the sortKey to the param passed
            $scope.reverse = !$scope.reverse; //if true make it false and vice versa
        }

        $scope.items = 5;
        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.posts = [];
        $scope.upVoted = false;
        $scope.downVoted = false;
        vm.showNoPostsMessage = false;
        $scope.currentUserUpvoteIds = [];
        var checkboxes = document.querySelectorAll('input[type="checkbox"]');
        var isOneChecked =
            vm.register = register;
        $scope.$on('authenticationSuccess', function () {
            getAccount();
            loadAll();
        });

        loadAll();

        function loadAll() {
            PostAdmin.query(function (result) {
                vm.posts = result;
                if (vm.posts.length === 0) {
                    vm.showNoPostsMessage = true;
                }
                vm.searchQuery = null;
            });
        }

        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
                $http.get("/api/currentUser/upVotes")
                    .success(function (response) {
                        $scope.currentUserUpvoteIds = response;
                        console.log(response);
                    })
                    .error(function (status, header) {
                    });
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        };

        $scope.isUpvoteDisabled = function (id) {
            return $scope.currentUserUpvoteIds.indexOf(id) >= 0;
        };


        function register() {
            $state.go('register');
        }

        function login() {
            collapseNavbar();
            LoginService.open();
        }

        $scope.upVote = function (id) {
            $http.put("/api/posts/" + id + "/upVote")
                .success(function (response) {
                    loadAll();
                    $scope.upVoted = true;
                    window.setTimeout(function () {
                        $scope.upVoted = false;
                        var alertElement = $('.alert');
                        alertElement.hide();
                    }, 2000);
                    $scope.isDisabled = true;
                })
                .error(function (status, header) {
                });
        };

        $scope.downVote = function (id) {
            $http.put("/api/posts/" + id + "/downVote")
                .success(function (status, headers) {
                    loadAll();
                    $scope.downVoted = true;
                    window.setTimeout(function () {
                        $scope.downVoted = false;
                        var alertElement = $('.alert');
                        alertElement.hide();
                    }, 2000);
                })
                .error(function (status, header) {
                });
        };

        $scope.sortKey = 'numberOfUpvotes';
        $scope.reverse = true;

    }
})();
