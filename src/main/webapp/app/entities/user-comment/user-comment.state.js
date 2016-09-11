(function() {
    'use strict';

    angular
        .module('mypropApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-comment', {
            parent: 'entity',
            url: '/user-comment',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.userComment.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-comment/user-comments.html',
                    controller: 'UserCommentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userComment');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-comment-detail', {
            parent: 'entity',
            url: '/user-comment/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mypropApp.userComment.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-comment/user-comment-detail.html',
                    controller: 'UserCommentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userComment');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserComment', function($stateParams, UserComment) {
                    return UserComment.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-comment',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-comment-detail.edit', {
            parent: 'user-comment-detail',
            url: '/detail/edit',
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
                        entity: ['UserComment', function(UserComment) {
                            return UserComment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-comment.new', {
            parent: 'user-comment',
            url: '/new',
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
                    $state.go('user-comment', null, { reload: true });
                }, function() {
                    $state.go('user-comment');
                });
            }]
        })
        .state('user-comment.edit', {
            parent: 'user-comment',
            url: '/{id}/edit',
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
                        entity: ['UserComment', function(UserComment) {
                            return UserComment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-comment', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-comment.delete', {
            parent: 'user-comment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-comment/user-comment-delete-dialog.html',
                    controller: 'UserCommentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserComment', function(UserComment) {
                            return UserComment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-comment', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
