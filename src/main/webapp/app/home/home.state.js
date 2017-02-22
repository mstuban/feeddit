(function() {
    'use strict';

    angular
        .module('feedditApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('home');
                    return $translate.refresh();
                }]
            }
        });

    $stateProvider.state('home.new', {
            parent: 'app',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/post/post-dialog.html',
                    controller: 'PostDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                submitDate: null,
                                authorID: null,
                                headline: null,
                                postURL: null,
                                authorName: null,
                                numberOfUpvotes: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('home', null, { reload: 'home' });
                }, function() {
                    $state.go('home');
                });
            }]
        });

    }
})();
