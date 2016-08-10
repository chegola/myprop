(function() {
    'use strict';

    angular
        .module('mypropApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('my-account', {
            parent: 'entity',
            url: '/my-account',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.myAccount.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/my-account/my-accounts.html',
                    controller: 'MyAccountController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('myAccount');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('my-account-detail', {
            parent: 'entity',
            url: '/my-account/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.myAccount.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/my-account/my-account-detail.html',
                    controller: 'MyAccountDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('myAccount');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'MyAccount', function($stateParams, MyAccount) {
                    return MyAccount.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'my-account',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('my-account-detail.edit', {
            parent: 'my-account-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/my-account/my-account-dialog.html',
                    controller: 'MyAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MyAccount', function(MyAccount) {
                            return MyAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('my-account.new', {
            parent: 'my-account',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/my-account/my-account-dialog.html',
                    controller: 'MyAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name_surname: null,
                                mobile: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('my-account', null, { reload: true });
                }, function() {
                    $state.go('my-account');
                });
            }]
        })
        .state('my-account.open', {
                    parent: 'my-account',
                    url: '/open',
                    data: {
                        authorities: ['ROLE_USER']
                    },
                    onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                        $uibModal.open({
                            templateUrl: 'app/entities/my-account/my-account-dialog-new.html',
                            controller: 'MyAccountDialogController',
                            controllerAs: 'vm',
                            backdrop: 'static',
                            size: 'lg',
                            resolve: {
                                entity: function () {
                                    return {
                                        name_surname: null,
                                        mobile: null,
                                        id: null
                                    };
                                }
                            }
                        }).result.then(function() {
                            $state.go('my-account', null, { reload: true });
                        }, function() {
                            $state.go('my-account');
                        });
                    }]
                })
        .state('my-account.edit', {
            parent: 'my-account',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/my-account/my-account-dialog.html',
                    controller: 'MyAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MyAccount', function(MyAccount) {
                            return MyAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('my-account', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('my-account.delete', {
            parent: 'my-account',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/my-account/my-account-delete-dialog.html',
                    controller: 'MyAccountDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MyAccount', function(MyAccount) {
                            return MyAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('my-account', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
