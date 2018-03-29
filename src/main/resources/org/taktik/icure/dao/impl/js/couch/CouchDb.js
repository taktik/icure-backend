function emit(id, val) {
}

var ICureDocument = {
  _id: "",
  deleted: "",
  java_type: "",
  delegations: [],
  secretForeignKeys: []
}

var Patient = {
  deleted: "",
  spouseName: "",
  maidenName: "",
  firstName: "",
  lastName: "",
  java_type: "",
  patientHealthCareParties: []
};

var Contact = {
  healthcarePartyId: "",
  responsible: "",
  closingDate: 0,
  openingDate: 0,
  subContacts: [],
  services: []
};

var SubContact = {
  formId: ""
};

var Invoice = {
  sentDate: 0
};

var HealthcareParty = {
  hcPartyKeys: {"123": ["key1", "Key2"]}  //Destination (delegate) the key encrypted for me, the key encrypted for him
};

var Service = {
  tags: [],
  codes: [],
  valueDate: 0
};

var Connector = {
  identifier: "",
  virtualHosts: []
};

var Property = {
  type: {
    identifier: ""
  }
};

var Code = {
  code: "",
  type: "",
  version: "",
  regions: [],
  searchTerms: {},
  label: {}
};
