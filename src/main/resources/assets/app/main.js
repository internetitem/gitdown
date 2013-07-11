var app = angular.module('gitdown', []);

app.controller('GitDownController', ['$scope', function($scope) {
    $scope.page = {
        title: 'Welcome to GitDown'
    }
}]);
