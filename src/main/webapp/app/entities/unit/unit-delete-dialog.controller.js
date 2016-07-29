(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('UnitDeleteController',UnitDeleteController);

    UnitDeleteController.$inject = ['$uibModalInstance', 'entity', 'Unit'];

    function UnitDeleteController($uibModalInstance, entity, Unit) {
        var vm = this;

        vm.unit = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Unit.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
