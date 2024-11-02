function updateLogicalUnitsDirective(){return{restrict:"E",templateUrl:"views/businessEntities/updateLogicalUnit.html",scope:{data:"=",logicalUnits:"=",logicalUnitIndex:"="},controller:function($scope,TDMService,BreadCrumbsService,toastr,$timeout){var updateLogicalUnitsCtrl=this;updateLogicalUnitsCtrl.logicalUnit=$scope.logicalUnits[$scope.logicalUnitIndex],updateLogicalUnitsCtrl.attachedLogicalUnits=angular.copy($scope.logicalUnits),updateLogicalUnitsCtrl.attachedLogicalUnits.splice($scope.logicalUnitIndex,1),updateLogicalUnitsCtrl.attachedLogicalUnits=_.map(updateLogicalUnitsCtrl.attachedLogicalUnits,(function(lu){return{lu_id:lu.lu_id,lu_name:lu.lu_name}})),TDMService.getLogicalUnits().then((function(response){const foundLu=response.result.find(it=>it.lu_name===updateLogicalUnitsCtrl.logicalUnit.lu_name);updateLogicalUnitsCtrl.attachedLogicalUnits=updateLogicalUnitsCtrl.attachedLogicalUnits.filter(it=>foundLu.lu_parents.indexOf(it.lu_name)>=0)})),TDMService.getGenericAPI("dataCenters").then((function(response){updateLogicalUnitsCtrl.dataCenters=_.unique(response.result.filter(item=>"ALIVE"===item.status),"dc")})),updateLogicalUnitsCtrl.updateLogicalUnit=function(){if(updateLogicalUnitsCtrl.logicalUnit.lu_parent_name){var temp=_.find(updateLogicalUnitsCtrl.attachedLogicalUnits,{lu_name:updateLogicalUnitsCtrl.logicalUnit.lu_parent_name});temp&&(updateLogicalUnitsCtrl.logicalUnit.lu_parent_id=temp.lu_id)}else delete updateLogicalUnitsCtrl.logicalUnit.lu_parent_id;TDMService.putLogicalUnit(updateLogicalUnitsCtrl.logicalUnit).then((function(response){"SUCCESS"==response.errorCode?(updateLogicalUnitsCtrl.logicalUnit.lu_parent_name||(updateLogicalUnitsCtrl.logicalUnit.lu_parent_name=""),$scope.logicalUnits[$scope.logicalUnitIndex]=updateLogicalUnitsCtrl.logicalUnit,$scope.data.reloadData(),updateLogicalUnitsCtrl.close(),toastr.success("Logical unit updated successfully")):updateLogicalUnitsCtrl.logicalUnitAlert={type:"danger",msg:"failed to Add Logical Units ["+response.message+"]"}}))},updateLogicalUnitsCtrl.closeAlert=function(){delete updateLogicalUnitsCtrl.logicalUnitAlert},updateLogicalUnitsCtrl.close=function(){$scope.data.close()}},controllerAs:"updateLogicalUnitsCtrl"}}angular.module("TDM-FE").directive("updateLogicalUnitsDirective",updateLogicalUnitsDirective);