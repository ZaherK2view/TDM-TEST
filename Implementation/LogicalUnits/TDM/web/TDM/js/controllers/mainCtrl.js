function MainCtrl($scope,$rootScope,$state,TDMService,BreadCrumbsService,$timeout,AuthService,FE_VERSION,DASHBOARD){var mainCtrl=this;mainCtrl.helloText="Welcome To TDM",mainCtrl.displayDashboard=DASHBOARD.display,mainCtrl.descriptionText="",mainCtrl.state=$state.current.name,mainCtrl.stateRef=$state,mainCtrl.environmentID=null,TDMService.getTDMVersion().then(response=>{mainCtrl.version=response.result}),mainCtrl.currentYear=(new Date).getFullYear(),mainCtrl.username=AuthService.getDisplayName(),mainCtrl.showTooltip=!0,mainCtrl.stateGo=function(state){$state.go(state)},$rootScope.interval="Month",mainCtrl.changeInterval=function(){$scope.$broadcast("intervalChanged",{interval:mainCtrl.interval})},mainCtrl.refreshPage=function(){console.log("reload page"),$rootScope.$broadcast("refreshPage",!0)},mainCtrl.openEnvironments=function(){$timeout((function(){$state.go("environments")}))},mainCtrl.openCreateProduct=function(){$timeout((function(){$state.go("newProduct")}))},mainCtrl.openCreateDataCenter=function(){$timeout((function(){$state.go("newDataCenter")}))},mainCtrl.openMain=function(){$state.go("tasks")},mainCtrl.updateBreadCrumb=function(breadCrumb){BreadCrumbsService.breadCrumbChange(breadCrumb.click),breadCrumb.callback(breadCrumb)},BreadCrumbsService.init(),mainCtrl.BreadCrumbs=BreadCrumbsService.getAll(),BreadCrumbsService.push({},"HOME",(function(){mainCtrl.openMain()}))}angular.module("TDM-FE").controller("MainCtrl",MainCtrl);