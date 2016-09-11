(function() {
    'use strict';

    angular
        .module('mypropApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('announcement', {
            parent: 'entity',
            url: '/announcement?page&sort&search',
            data: {
                //authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.announcement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/announcement/announcements-list.html',
                    controller: 'AnnouncementController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'startDate,desc',
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
                    $translatePartialLoader.addPart('announcement');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('announcement-ori', {
            parent: 'entity',
            url: '/announcement?page&sort&search',
            data: {
                //authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.announcement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/announcement/announcements.html',
                    controller: 'AnnouncementController',
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
                    $translatePartialLoader.addPart('announcement');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('announcement-detail', {
            parent: 'entity',
            url: '/announcement/{id}',
            data: {
                //authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.announcement.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/announcement/announcement-detail-view.html',
                    controller: 'AnnouncementDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('announcement');
                    $translatePartialLoader.addPart('userComment');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Announcement', function($stateParams, Announcement) {
                    return Announcement.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'announcement',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })

        .state('announcement-detail.user-comment-new', {
           // parent: 'announcement-detail',
            url: '/user-comment-new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-comment/user-comment-dialog.html',
                    controller: 'UserCommentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                comment: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('announcement-detail', {id : $stateParams.id}, { reload: true });
                }, function() {
                    $state.go('announcement-detail', {id : $stateParams.id}, { reload: false });
                });
            }]
        })
        .state('announcement-detail.edit', {
            parent: 'announcement-detail',
            url: '/detail/edit',
            data: {
               authorities: ['ROLE_ADMIN', 'ROLE_MANAGER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    //templateUrl: 'app/entities/announcement/announcement-dialog.html',
                    templateUrl: 'app/entities/announcement/announcement-dialog-update.html',
                    controller: 'AnnouncementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Announcement', function(Announcement) {
                            return Announcement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('announcement.new', {
            parent: 'announcement',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_MANAGER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    //templateUrl: 'app/entities/announcement/announcement-dialog.html',
                    templateUrl: 'app/entities/announcement/announcement-dialog-new.html',
                    controller: 'AnnouncementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                subject: null,
                                detail: null,
                                startDate: null,
                                endDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('announcement', null, { reload: true });
                }, function() {
                    $state.go('announcement');
                });
            }]
        })
        .state('announcement.edit', {
            parent: 'announcement',
            url: '/{id}/edit',
            data: {
               authorities: ['ROLE_ADMIN', 'ROLE_MANAGER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/announcement/announcement-dialog.html',
                    controller: 'AnnouncementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Announcement', function(Announcement) {
                            return Announcement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('announcement', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('announcement.delete', {
            parent: 'announcement',
            url: '/{id}/delete',
            data: {
               authorities: ['ROLE_ADMIN', 'ROLE_MANAGER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/announcement/announcement-delete-dialog.html',
                    controller: 'AnnouncementDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Announcement', function(Announcement) {
                            return Announcement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('announcement', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
