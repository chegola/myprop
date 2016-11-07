(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('LineDialogController', LineDialogController);

    LineDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Line'];

    function LineDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Line) {
        var vm = this;

        vm.line = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.line.id !== null) {
                Line.update(vm.line, onSaveSuccess, onSaveError);
            } else {
                Line.save(vm.line, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mypropApp:lineUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.timestamp = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
