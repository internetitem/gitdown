var app = angular.module('gitdown', []);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'pages/main.html',
        controller: 'MainPageController'
    });
}]);

app.controller('GitDownController', ['$scope', function($scope) {
    $scope.page = {
        title: 'Welcome to GitDown'
    }
}]);

app.controller('MainPageController', ['$scope', function($scope) {
    $scope.page.title = 'Main Page';
}]);


