import {flatMap} from "lodash"
import {Environment} from "evaljs"
import {get} from "request";

const ctxts =  flatMap([true, false], conv => flatMap([true, false], trainee => flatMap([true, false], chron => flatMap([true, false], dmg => flatMap([true, false], ps => flatMap([0,30,77],(age => ({
    label: `${trainee?'tr':'dr'} ${conv?'cv':'nc'}, pat:${ps?'bim':'reg'} ${dmg?'dmgok':'nodmg'} ${chron?'chr':'---'} age:${age}`,
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
  }))))))));

get({url:`http://${process.argv[2] + ':' + process.argv[3]}@127.0.0.1:16043/rest/v1/tarification/INAMI-RIZIV|${process.argv[4]}|1.0`, json:true}, (req,res,c) => {
  console.log(JSON.stringify(ctxts.map(ctx => {
      const env = new Environment(ctx)
      return {
        label: ctx.label,
        vals: c.valorisations.filter(val => val.startOfValidity/10000000000 >= 2018 && val.reimbursement>0).filter(val => {
          const gen = (env.gen(val.predicate)())
          let status = {done: false, value: false}

          while (!status.done) {
            status = gen.next() //Execute lines one by one
          }
          return status.value
        }).map(val => [val.predicate, val.reimbursement])
      }
    })))
  })
