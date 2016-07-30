(function() {
    'use strict';

    angular
        .module('mypropApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('unit', {
            parent: 'entity',
            url: '/unit?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.unit.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/unit/units.html',
                    controller: 'UnitController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('unit');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('unit-detail', {
            parent: 'entity',
            url: '/unit/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.unit.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/unit/unit-detail.html',
                    controller: 'UnitDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('unit');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Unit', function($stateParams, Unit) {
                    return Unit.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'unit',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('unit-detail.edit', {
            parent: 'unit-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/unit/unit-dialog.html',
                    controller: 'UnitDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Unit', function(Unit) {
                            return Unit.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('unit.new', {
            parent: 'unit',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/unit/unit-dialog.html',
                    controller: 'UnitDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                externalId: null,
                                area: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('unit', null, { reload: true });
                }, function() {
                    $state.go('unit');
                });
            }]
        })
        .state('unit.edit', {
            parent: 'unit',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/unit/unit-dialog.html',
                    controller: 'UnitDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Unit', function(Unit) {
                            return Unit.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('unit', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('unit.delete', {
            parent: 'unit',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/unit/unit-delete-dialog.html',
                    controller: 'UnitDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Unit', function(Unit) {
                            return Unit.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('unit', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
