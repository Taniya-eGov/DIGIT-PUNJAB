import React from "react";

import {
  initPGRComponents,
  PGRReducers,
} from "@egovernments/digit-ui-module-pgr";
import { initFSMComponents } from "@egovernments/digit-ui-module-fsm";
// import {
//   PTModule,
//   PTLinks,
//   PTComponents,
// } from "@egovernments/digit-ui-module-pt";
// import { MCollectModule, MCollectLinks, initMCollectComponents } from "@egovernments/digit-ui-module-mcollect";
import { initDSSComponents } from "@egovernments/digit-ui-module-dss";
import {
  PaymentModule,
  PaymentLinks,
  paymentConfigs,
} from "@egovernments/digit-ui-module-common";
import { DigitUI } from "@egovernments/digit-ui-module-core";
import { initLibraries } from "@egovernments/digit-ui-libraries";
// import { HRMSModule ,initHRMSComponents  } from "@egovernments/digit-ui-module-hrms";
// import { TLModule, TLLinks,initTLComponents } from "@egovernments/digit-ui-module-tl";
// import { initReceiptsComponents, ReceiptsModule } from "@egovernments/digit-ui-module-receipts";
// import { initOBPSComponents } from "@egovernments/digit-ui-module-obps";
// import { initNOCComponents } from "@egovernments/digit-ui-module-noc";
import { initEngagementComponents } from "@egovernments/digit-ui-module-engagement";
// import { initWSComponents } from "@egovernments/digit-ui-module-ws";
// import { initCommonPTComponents } from "@egovernments/digit-ui-module-commonpt";

import { customizations, initCustomisationComponents } from "./Customisations";

initLibraries();
//"WS" removed the ws enabledModules ;
const enabledModules = [
  "PGR",
  "FSM",
  "Payment",
  "PT",
  "QuickPayLinks",
  "DSS",
  "NDSS",
  "MCollect",
  "HRMS",
  "TL",
  "Receipts",
  "OBPS",
  "NOC",
  "Engagement",
  "CommonPT",
];
window.Digit.ComponentRegistryService.setupRegistry({
  ...paymentConfigs,

  PaymentModule,
  PaymentLinks,
  // PTModule,
  // PTLinks,
  // ...PTComponents,
  // MCollectLinks,
  // MCollectModule,
  // HRMSModule,
  // TLModule,
  // TLLinks,
  // ReceiptsModule
});

initPGRComponents();
initFSMComponents();
initDSSComponents();
// initMCollectComponents();
// initHRMSComponents()
// initTLComponents();
// initReceiptsComponents();
// initOBPSComponents();
// initNOCComponents();
initEngagementComponents();
// initWSComponents();
// initCommonPTComponents();
initCustomisationComponents();

const moduleReducers = (initData) => ({
  pgr: PGRReducers(initData),
});

function App() {
  customizations.forEach((setup) => setup());
  const stateCode =
    window.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID") ||
    process.env.REACT_APP_STATE_LEVEL_TENANT_ID;
  // const stateCode = "od";
  if (!stateCode) {
    return <h1>stateCode is not defined</h1>;
  }
  return (
    <DigitUI
      stateCode={stateCode}
      enabledModules={enabledModules}
      moduleReducers={moduleReducers}
    />
  );
}

export default App;
