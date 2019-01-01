var icc_api_1 = require("icc-api");
var lodash_1 = require("lodash");
var evaljs_1 = require("evaljs");
var icc = new icc_api_1.iccTarificationApi("http://127.0.0.1:16043/rest/v1", [new icc_api_1.XHR.Header('Authentication', 'Basic ' + btoa(process.argv[1] + ':' + process.argv[2]))]);
var ctxts = lodash_1["default"].flatMap([true, false], function (conv) { return lodash_1["default"].flatMap([true, false], function (trainee) { return lodash_1["default"].flatMap([true, false], function (chron) { return lodash_1["default"].flatMap([true, false], function (dmg) { return lodash_1["default"].flatMap([true, false], function (ps) { return lodash_1["default"].flatMap([0, 1, 5, 12, 30, 77], (function (age) { return ({
    label: (trainee ? 'tr' : 'dr') + " " + (conv ? 'cv' : 'nc') + ", pat:" + (ps ? 'bim' : 'reg') + " " + (dmg ? 'dmgok' : 'nodmg') + " " + (chron ? 'chr' : '---') + " age:" + age,
    patient: {
        preferentialstatus: ps,
        age: age,
        dmg: dmg,
        chronical: chron
    },
    hcp: {
        trainee: trainee,
        convention: conv
    }
}); })); }); }); }); }); });
icc.getTarification("INAMI-RIZIV|" + process.argv[3] + "|1").then(function (c) {
    return ctxts.map(function (ctx) {
        var env = new evaljs_1["default"].Environment(ctx);
        return {
            label: ctx.label,
            vals: c.valorisations.filter(function (val) {
                var gen = (env.gen(val.predicate)());
                var status = { done: false, value: false };
                while (!status.done) {
                    status = gen.next(); //Execute lines one by one
                }
                return status.value;
            }).map(function (val) { return val.reimbursement; })
        };
    });
});
