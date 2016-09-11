(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('UserCommentController', UserCommentController);

    UserCommentController.$inject = ['$scope', '$state', 'UserComment'];

    function UserCommentController ($scope, $state, UserComment) {
        var vm = this;
        
        vm.userComments = [];

        loadAll();

        function loadAll() {
            UserComment.query(function(result) {
                vm.userComments = result;
            });
        }
    }
})();
