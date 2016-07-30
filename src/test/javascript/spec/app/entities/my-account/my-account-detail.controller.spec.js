'use strict';

describe('Controller Tests', function() {

    describe('MyAccount Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockMyAccount, MockUser, MockUnit;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockMyAccount = jasmine.createSpy('MockMyAccount');
            MockUser = jasmine.createSpy('MockUser');
            MockUnit = jasmine.createSpy('MockUnit');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'MyAccount': MockMyAccount,
                'User': MockUser,
                'Unit': MockUnit
            };
            createController = function() {
                $injector.get('$controller')("MyAccountDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mypropApp:myAccountUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
