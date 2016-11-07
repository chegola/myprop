(function() {
    'use strict';

    angular
        .module('mypropApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('line', {
            parent: 'entity',
            url: '/line?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.line.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/line/lines.html',
                    controller: 'LineController',
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
                    $translatePartialLoader.addPart('line');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('line-detail', {
            parent: 'entity',
            url: '/line/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.line.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/line/line-detail.html',
                    controller: 'LineDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('line');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Line', function($stateParams, Line) {
                    return Line.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'line',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('line-detail.edit', {
            parent: 'line-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/line/line-dialog.html',
                    controller: 'LineDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Line', function(Line) {
                            return Line.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('line.new', {
            parent: 'line',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/line/line-dialog.html',
                    controller: 'LineDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                sourceId: null,
                                sourceType: null,
                                active: null,
                                timestamp: null,
                                displayName: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('line', null, { reload: true });
                }, function() {
                    $state.go('line');
                });
            }]
        })
        .state('line.edit', {
            parent: 'line',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/line/line-dialog.html',
                    controller: 'LineDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Line', function(Line) {
                            return Line.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('line', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('line.delete', {
            parent: 'line',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/line/line-delete-dialog.html',
                    controller: 'LineDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Line', function(Line) {
                            return Line.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('line', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
