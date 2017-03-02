(function () {
    'use strict';

    angular
        .module('feedditApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['PostAdmin', '$scope', '$stateParams', 'Principal', 'LoginService', '$state', '$http', 'AlertService'];

    function HomeController(PostAdmin, $scope, $stateParams, Principal, LoginService, $state, $http, AlertService) {
        var vm = this;

        if ($stateParams.loggedOut) {
            AlertService.success("You have successfully logged out!");
        }

        $scope.sort = function (keyname) {
            $scope.sortKey = keyname;   //set the sortKey to the param passed
            $scope.reverse = !$scope.reverse; //if true make it false and vice versa
        };

        $scope.items = 5;
        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.posts = [];
        vm.showNoPostsMessage = false;
        $scope.currentUserUpvoteIds = [];
        $scope.currentUserDownvoteIds = [];

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
                vm.isAuthenticated = Principal.isAuthenticated;

                $http.get("/api/currentUser/upVotes")
                    .success(function (response) {
                        $scope.currentUserUpvoteIds = response;
                    })
                    .error(function (status, header) {
                    });

                $http.get("/api/currentUser/downVotes")
                    .success(function (response) {
                        $scope.currentUserDownvoteIds = response;
                    })
                    .error(function (status, header) {
                    });


            });
        }

        function register() {
            $state.go('register');
        }

        $scope.upVote = function (id) {
            $http.put("/api/posts/" + id + "/upVote")
                .success(function () {
                    loadAll();
                    AlertService.success("Post upvoted!");
                    if (!$scope.currentUserUpvoteIds.isArray) {
                        $scope.currentUserUpvoteIds = [];
                    }
                    $scope.currentUserUpvoteIds.push(id);
                    if ($scope.currentUserDownvoteIds.indexOf(id) > -1) {
                        $scope.currentUserDownvoteIds.splice($scope.currentUserDownvoteIds.indexOf(id), 1);
                    }
                })
                .error(function (status, header) {
                });
        };

        $scope.downVote = function (id) {
            $http.put("/api/posts/" + id + "/downVote")
                .success(function () {
                    loadAll();
                    AlertService.success("Post downvoted!");
                    if (!$scope.currentUserDownvoteIds.isArray) {
                        $scope.currentUserDownvoteIds = [];
                    }
                    $scope.currentUserDownvoteIds.push(id);
                    if ($scope.currentUserUpvoteIds.indexOf(id) > -1) {
                        $scope.currentUserUpvoteIds.splice($scope.currentUserUpvoteIds.indexOf(id), 1);
                    }
                })
                .error(function (status, header) {
                });
        };

        $scope.sortKey = 'numberOfUpvotes';
        $scope.reverse = true;

    }
})();
