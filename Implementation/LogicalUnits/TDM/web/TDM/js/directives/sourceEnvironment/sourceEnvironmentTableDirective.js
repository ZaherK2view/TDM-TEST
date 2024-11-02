function sourceEnvironmentsTableDirective(){return{restrict:"E",templateUrl:"views/soruceEnvrionments/sourceEnvironmentsTable.html",scope:{content:"="},controller:function($scope,$compile,TDMService,DTColumnBuilder,DTOptionsBuilder,$q,$timeout){var sourceEnvironmentsTableCtrl=this;sourceEnvironmentsTableCtrl.loadingTable=!0,TDMService.getGenericAPI("sourceEnvironments").then((function(response){if("SUCCESS"==response.errorCode){sourceEnvironmentsTableCtrl.environmentsData=_.sortBy(response.result,(function(value){return new Date(value.environment_creation_date)})),sourceEnvironmentsTableCtrl.environmentsData.reverse(),sourceEnvironmentsTableCtrl.dtInstance={},sourceEnvironmentsTableCtrl.dtColumns=[],sourceEnvironmentsTableCtrl.dtColumnDefs=[],sourceEnvironmentsTableCtrl.headers=[{column:"source_environment_name",name:"Name",clickAble:!0},{column:"environment_point_of_contact_first_name",name:"Contact First Name",clickAble:!1},{column:"environment_point_of_contact_last_name",name:"Contact Last Name",clickAble:!1},{column:"environment_point_of_contact_email",name:"Contact Email",clickAble:!1},{column:"environment_creation_date",name:"Creation Date",clickAble:!1,type:"date"},{column:"environment_created_by",name:"Created By",clickAble:!1},{column:"environment_last_updated_date",name:"Last Update Date",clickAble:!1,type:"date"},{column:"environment_last_updated_by",name:"Updated By",clickAble:!1},{column:"environment_status",name:"Status",clickAble:!1}];for(var clickAbleColumn=function(data,type,full,meta){return'<a ng-click="sourceEnvironmentsTableCtrl.openEnvironment('+full.source_environment_id+')">'+data+"</a>"},changeToLocalDate=function(data,type,full,meta){return data?moment(data).format("DD MMM YYYY, HH:mm"):""},i=0;i<sourceEnvironmentsTableCtrl.headers.length;i++)1==sourceEnvironmentsTableCtrl.headers[i].clickAble?sourceEnvironmentsTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(sourceEnvironmentsTableCtrl.headers[i].column).withTitle(sourceEnvironmentsTableCtrl.headers[i].name).renderWith(clickAbleColumn)):"date"==sourceEnvironmentsTableCtrl.headers[i].type?sourceEnvironmentsTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(sourceEnvironmentsTableCtrl.headers[i].column).withTitle(sourceEnvironmentsTableCtrl.headers[i].name).renderWith(changeToLocalDate)):sourceEnvironmentsTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(sourceEnvironmentsTableCtrl.headers[i].column).withTitle(sourceEnvironmentsTableCtrl.headers[i].name));var getTableData=function(){var deferred=$q.defer();return deferred.resolve(sourceEnvironmentsTableCtrl.environmentsData),deferred.promise};sourceEnvironmentsTableCtrl.dtOptions=DTOptionsBuilder.fromFnPromise((function(){return getTableData()})).withOption("aaSorting",[8,"asc"]).withDOM('<"html5buttons"B>lTfgitp').withOption("createdRow",(function(row){$compile(angular.element(row).contents())($scope)})).withOption("scrollX",!1).withButtons([]).withOption("caseInsensitive",!0).withOption("search",{caseInsensitive:!1}),sourceEnvironmentsTableCtrl.environmentsData&&sourceEnvironmentsTableCtrl.environmentsData.length>0&&sourceEnvironmentsTableCtrl.dtOptions.withLightColumnFilter({0:{type:"text"},1:{type:"text"},2:{type:"text"},3:{type:"text"},4:{type:"text"},5:{type:"select",values:_.map(_.unique(_.map(sourceEnvironmentsTableCtrl.environmentsData,"environment_created_by")),(function(el){return{value:el,label:el}}))},6:{type:"text"},7:{type:"select",values:_.map(_.unique(_.map(sourceEnvironmentsTableCtrl.environmentsData,"environment_last_updated_by")),(function(el){return{value:el,label:el}}))},8:{type:"select",defaultValue:"Active",values:[{value:"Inactive",label:"Inactive"},{value:"Active",label:"Active"}]}}),sourceEnvironmentsTableCtrl.dtInstanceCallback=function(dtInstance){angular.isFunction(sourceEnvironmentsTableCtrl.dtInstance)?sourceEnvironmentsTableCtrl.dtInstance(dtInstance):angular.isDefined(sourceEnvironmentsTableCtrl.dtInstance)&&(sourceEnvironmentsTableCtrl.dtInstance=dtInstance)},null!=sourceEnvironmentsTableCtrl.dtInstance.changeData&&sourceEnvironmentsTableCtrl.dtInstance.changeData(getTableData()),$timeout(()=>{sourceEnvironmentsTableCtrl.loadingTable=!1})}})),sourceEnvironmentsTableCtrl.openEnvironment=function(environmentID){if($scope.content.openEnvironment){var environmentData=_.find(sourceEnvironmentsTableCtrl.environmentsData,{source_environment_id:environmentID});if(environmentData)return void $scope.content.openEnvironment(environmentData)}},sourceEnvironmentsTableCtrl.openNewEnvironment=function(){$scope.content.openNewEnvironment&&$scope.content.openNewEnvironment(sourceEnvironmentsTableCtrl.environmentsData)}},controllerAs:"sourceEnvironmentsTableCtrl"}}angular.module("TDM-FE").directive("sourceEnvironmentsTableDirective",sourceEnvironmentsTableDirective);