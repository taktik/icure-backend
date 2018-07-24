class ISCO {

	// The letter “p” is used to indicate that only part of the ISCO-88 group corresponds with the ISCO-08 group.
	static def Isco88To08(String isco88){
		switch(isco88){
			case 1110:
				return 1111
				break
			case 1120:
				return 1112
				break
			case 1130:
				return 1113
				break
			case 1141:
			case 1142:
			case 1143:
				return 1114
				break
			case 1210:
				return 1120
				break
			case 1231:	//p
			case 1317:	//p
				return 1211
				break
			case 1232:
			case 1317:	//p
				return 1212
				break
			case 1229:	//p
			case 1239:
				return 1213
				break
			case 1227:	//p
				return 1219
				break
			case 1228:
			case 1229:	//p
			case 1231:	//p
			case 1317:	//p
			case 1318:
				return 1219
				break
			case 1233:
			case 1317:	//p
				return 1221
				break
			case 1234:
			case 1317:	//p
				return 1222
				break
			case 1237:
			case 1319:	//p
				return 1223
				break
			case 1221:	//p
				return 1311
				break
			case 1221:	//p
				return 1312
				break
			case 1222:	//p
			case 1312:	//p
				return 1321
				break
			case 1222:	//p
			case 1312:	//p
				return 1322
				break
			case 1223:	//p
			case 1313:
				return 1323
				break
			case 1226:	//p
			case 1235:
			case 1316:	//p
				return 1324
				break
			case 1226:	//p
			case 1236:
			case 1316:	//p
			case 1317:	//p
				return 1330
				break
			case 1229:	//p
			case 1319:	//p
				return 1341
				break
			case 1229:	//p
			case 1319:	//p
			case 2230:	//p
				return 1342
				break
			case 1229:	//p
			case 1319:	//p
			case 2230:	//p
				return 1343
				break
			case 1229:	//p
				return 1344
				break
			case 1319:	//p
				return 1344
				break
			case 1229:	//p
			case 1319:	//p
				return 1345
				break
			case 1227:	//p
			case 1317:	//p
				return 1346
				break
			case 1229:	//p
			case 1319:	//p
				return 1349
				break
			case 1225:	//p
			case 1315:	//p
				return 1411
				break
			case 1225:	//p
			case 1315:	//p
				return 1412
				break
			case 1224:
			case 1314:	//p
				return 1420
				break
			case 1319:	//p
				return 1431
				break
			case 1229:	//p
			case 1319:	//p
				return 1439
				break
			case 2111:
				return 2111
				break
			case 2112:
				return 2112
				break
			case 2113:	//p
				return 2113
				break
			case 2114:
				return 2114
				break
			case 2121:
			case 2122:
				return 2120
				break
			case 2211:	//p
			case 2212:	//p
				return 2131
				break
			case 2213:
			case 3213:
				return 2132
				break
			case 2211:	//p
				return 2133
				break
			case 2149:	//p
				return 2141
				break
			case 2142:
				return 2142
				break
			case 2149:	//p
				return 2143
				break
			case 2145:
				return 2144
				break
			case 2146:
				return 2145
				break
			case 2147:
				return 2146
				break
			case 2149:	//p
				return 2149
				break
			case 2143:
				return 2151
				break
			case 2144:	//p
				return 2152
				break
			case 2144:	//p
				return 2153
				break
			case 2141:	//p
				return 2161
				break
			case 2141:	//p
				return 2162
				break
			case 3471:	//p
				return 2163
				break
			case 2141:	//p
				return 2164
				break
			case 2148:
				return 2165
				break
			case 3471:	//p
			case 2452:	//p
				return 2166
				break
			case 2221:	//p
				return 2211
				break
			case 2212:	//p
			case 2221:	//p
				return 2212
				break
			case 2230:	//p
				return 2221
				break
			case 2230:	//p
				return 2222
				break
			case 3229:	//p
			case 3241:	//p
				return 2230
				break
			case 3221:	//p
				return 2240
				break
			case 2212:	//p
			case 2223:
				return 2250
				break
			case 2222:
				return 2261
				break
			case 2113:	//p
			case 2224:
				return 2262
				break
			case 2229:	//p
			case 2412:	//p
			case 3152:	//p
			case 3222:	//p
				return 2263
				break
			case 3226:	//p
				return 2264
				break
			case 3223:
				return 2265
				break
			case 3229:	//p
				return 2266
				break
			case 3224:	//p
			case 3229:	//p
				return 2267
				break
			case 2229:	//p
			case 3226:	//p
			case 3229:	//p
				return 2269
				break
			case 2310:	//p
				return 2310
				break
			case 2310:	//p
			case 2320:	//p
				return 2320
				break
			case 2320:	//p
				return 2330
				break
			case 2331:
			case 3310:
				return 2341
				break
			case 2332:
			case 3320:
				return 2342
				break
			case 2351:
			case 2352:
				return 2351
				break
			case 2340:
			case 3330:
				return 2352
				break
			case 2359:	//p
			case 3340:	//p
				return 2353
				break
			case 2359:	//p
				return 2354
				break
			case 2359:	//p
			case 3340:	//p
				return 2355
				break
			case 2359:	//p
			case 3340:	//p
				return 2356
				break
			case 2359:	//p
			case 3340:	//p
				return 2359
				break
			case 2411:	//p
				return 2411
				break
			case 2411:	//p
			case 2419:	//p
			case 3411:	//p
				return 2412
				break
			case 2419:	//p
				return 2413
				break
			case 2419:	//p
				return 2421
				break
			case 2419:	//p
				return 2422
				break
			case 2412:	//p
				return 2423
				break
			case 2412:	//p
				return 2424
				break
			case 2419:	//p
			case 2451:	//p
				return 2431
				break
			case 2419:	//p
			case 2451:	//p
				return 2432
				break
			case 3415:	//p
				return 2433
				break
			case 3415:	//p
				return 2434
				break
			case 2131:	//p
				return 2511
				break
			case 2131:	//p
				return 2512
				break
			case 2131:	//p
			case 2132:	//p
			case 2139:	//p
				return 2513
				break
			case 2132:	//p
				return 2514
				break
			case 2131:	//p
			case 2132:	//p
			case 2139:	//p
				return 2519
				break
			case 2131:	//p
				return 2521
				break
			case 2131:	//p
				return 2522
				break
			case 2131:	//p
			case 2132:	//p
				return 2523
				break
			case 2139:	//p
				return 2529
				break
			case 2421:
				return 2611
				break
			case 2422:
				return 2612
				break
			case 2429:
				return 2619
				break
			case 2431:
				return 2621
				break
			case 2432:
				return 2622
				break
			case 2441:
				return 2631
				break
			case 2442:
				return 2632
				break
			case 2443:
				return 2633
				break
			case 2445:
				return 2634
				break
			case 2446:
				return 2635
				break
			case 2460:
				return 2636
				break
			case 2451:	//p
				return 2641
				break
			case 2451:	//p
			case 3472:	//p
				return 2642
				break
			case 2444:
				return 2643
				break
			case 2452:	//p
				return 2651
				break
			case 2453:
			case 3473:	//p
				return 2652
				break
			case 2454:
			case 3473:	//p
				return 2653
				break
			case 1229:	//p
			case 2455:	//p
				return 2654
				break
			case 2455:	//p
				return 2655
				break
			case 3472:	//p
				return 2656
				break
			case 3474:	//p
				return 2659
				break
			case 3111:
				return 3111
				break
			case 3112:
			case 3151:	//p
				return 3112
				break
			case 3113:
			case 3152:	//p
				return 3113
				break
			case 3114:	//p
			case 3152:	//p
				return 3114
				break
			case 3115:
			case 3152:	//p
				return 3115
				break
			case 3116:	//p
				return 3116
				break
			case 3117:
			case 3152:	//p
				return 3117
				break
			case 3118:
				return 3118
				break
			case 3119:
			case 3151:	//p
				return 3119
				break
			case 7111:	//p
			case 8111:	//p
				return 3121
				break
			case 8171:	//p
			case 8172:	//p
			case 8211:	//p
			case 8221:	//p
			case 8222:	//p
			case 8223:	//p
			case 8224:	//p
			case 8229:	//p
			case 8231:	//p
			case 8232:	//p
			case 8240:	//p
			case 8251:	//p
			case 8252:	//p
			case 8253:	//p
			case 8261:	//p
			case 8262:	//p
			case 8263:	//p
			case 8264:	//p
			case 8265:	//p
			case 8266:	//p
			case 8269:	//p
			case 8271:	//p
			case 8272:	//p
			case 8273:	//p
			case 8274:	//p
			case 8275:	//p
			case 8276:	//p
			case 8277:	//p
			case 8278:	//p
			case 8279:	//p
			case 8281:	//p
			case 8282:	//p
			case 8283:	//p
			case 8284:	//p
			case 8285:	//p
			case 8286:	//p
			case 8290:	//p
				return 3122
				break
			case 1223:	//p
			case 7129:	//p
				return 3123
				break
			case 8161:
				return 3131
				break
			case 8163:
				return 3132
				break
			case 3116:	//p
			case 8152:	//p
			case 8153:	//p
			case 8154:	//p
			case 8159:	//p
				return 3133
				break
			case 8155:	//p
				return 3134
				break
			case 8121:	//p
			case 8122:	//p
			case 8123:	//p
			case 8124:	//p
				return 3135
				break
			case 3123:
			case 8142:	//p
			case 8143:	//p
			case 8171:	//p
			case 8172:	//p
				return 3139
				break
			case 3211:	//p
				return 3141
				break
			case 3212:	//p
				return 3142
				break
			case 3212:	//p
				return 3143
				break
			case 3141:
				return 3151
				break
			case 3142:
				return 3152
				break
			case 3143:
			case 3340:	//p
				return 3153
				break
			case 3144:
				return 3154
				break
			case 3145:
				return 3155
				break
			case 3133:
				return 3211
				break
			case 3211:	//p
				return 3212
				break
			case 3228:
				return 3213
				break
			case 3226:	//p
			case 7311:	//p
				return 3214
				break
			case 2230:	//p
			case 3231:
				return 3221
				break
			case 2230:	//p
			case 3232:
				return 3222
				break
			case 3241:	//p
				return 3230
				break
			case 3227:
				return 3240
				break
			case 3225:
				return 3251
				break
			case 4143:	//p
				return 3252
				break
			case 3221:	//p
				return 3253
				break
			case 3224:	//p
				return 3254
				break
			case 3226:	//p
				return 3255
				break
			case 3221:	//p
				return 3256
				break
			case 3152:	//p
			case 3222:	//p
				return 3257
				break
			case 5132:	//p
				return 3258
				break
			case 3229:	//p
				return 3259
				break
			case 3411:	//p
				return 3311
				break
			case 3419:	//p
				return 3312
				break
			case 3433:
			case 3434:	//p
				return 3313
				break
			case 3434:	//p
				return 3314
				break
			case 3417:	//p
				return 3315
				break
			case 3412:
				return 3321
				break
			case 3415:	//p
				return 3322
				break
			case 3416:
				return 3323
				break
			case 3421:
				return 3324
				break
			case 3422:
				return 3331
				break
			case 3439:	//p
				return 3332
				break
			case 3423:
				return 3333
				break
			case 3413:
				return 3334
				break
			case 2419:	//p
			case 3414:	//p
			case 3417:	//p
			case 3429:	//p
				return 3339
				break
			case 3431:	//p
			case 3439:	//p
			case 4111:	//p
			case 4112:	//p
			case 4113:	//p
			case 4114:	//p
			case 4115:	//p
			case 4121:	//p
			case 4122:	//p
			case 4131:	//p
			case 4132:	//p
			case 4133:	//p
			case 4141:	//p
			case 4142:	//p
			case 4143:	//p
			case 4190:	//p
			case 4222:	//p
			case 4223:	//p
				return 3341
				break
			case 3431:	//p
			case 4115:	//p
				return 3342
				break
			case 3431:	//p
			case 3439:	//p
				return 3343
				break
			case 3431:	//p
			case 4115:	//p
				return 3344
				break
			case 3441:
				return 3351
				break
			case 3442:
				return 3352
				break
			case 3443:
				return 3353
				break
			case 3444:
				return 3354
				break
			case 3450:	//p
				return 3355
				break
			case 3439:	//p
			case 3449:
				return 3359
				break
			case 3432:
			case 3450:	//p
				return 3411
				break
			case 3460:
				return 3412
				break
			case 3242:
			case 3480:
				return 3413
				break
			case 3475:	//p
				return 3421
				break
			case 3475:	//p
				return 3422
				break
			case 3340:	//p
				return 3423
				break
			case 3475:	//p
				return 3423
				break
			case 3131:	//p
				return 3431
				break
			case 3471:	//p
				return 3432
				break
			case 3439:	//p
			case 3471:	//p
				return 3433
				break
			case 5122:	//p
				return 3434
				break
			case 1229:	//p
				return 3435
				break
			case 3471:	//p
				return 3435
				break
			case 3474:	//p
				return 3435
				break
			case 3122:
				return 3511
				break
			case 3121:	//p
				return 3512
				break
			case 3121:	//p
				return 3513
				break
			case 3121:	//p
				return 3514
				break
			case 3131:	//p
			case 3132:	//p
				return 3521
				break
			case 3114:	//p
			case 3132:	//p
				return 3522
				break
			case 4190:	//p
				return 4110
				break
			case 4115:	//p
				return 4120
				break
			case 4111:	//p
			case 4112:	//p
				return 4131
				break
			case 4113:	//p
			case 4114:	//p
				return 4132
				break
			case 4211:	//p
			case 4212:
				return 4211
				break
			case 4211:	//p
			case 4213:
				return 4212
				break
			case 4214:
				return 4213
				break
			case 4215:
				return 4214
				break
			case 3414:	//p
			case 4221:
			case 5111:	//p
				return 4221
				break
			case 4222:	//p
				return 4222
				break
			case 4223:	//p
				return 4223
				break
			case 4222:	//p
				return 4224
				break
			case 4222:	//p
				return 4225
				break
			case 4222:	//p
				return 4226
				break
			case 4190:	//p
				return 4227
				break
			case 4222:	//p
				return 4229
				break
			case 4121:	//p
				return 4311
				break
			case 4122:	//p
				return 4312
				break
			case 4121:	//p
				return 4313
				break
			case 4131:	//p
				return 4321
				break
			case 4132:	//p
				return 4322
				break
			case 4133:	//p
				return 4323
				break
			case 4141:	//p
				return 4411
				break
			case 4142:	//p
				return 4412
				break
			case 4143:	//p
				return 4413
				break
			case 4144:	//p
				return 4414
				break
			case 4141:	//p
				return 4415
				break
			case 4190:	//p
				return 4416
				break
			case 4190:	//p
				return 4419
				break
			case 5111:	//p
				return 5111
				break
			case 5112:
				return 5112
				break
			case 5113:
				return 5113
				break
			case 5122:	//p
				return 5120
				break
			case 5123:	//p
				return 5131
				break
			case 5123:	//p
				return 5132
				break
			case 5141:	//p
				return 5141
				break
			case 5141:	//p
				return 5142
				break
			case 5121:	//p
				return 5151
				break
			case 5121:	//p
				return 5152
				break
			case 9141:
				return 5153
				break
			case 5151:
			case 5152:
				return 5161
				break
			case 5142:
				return 5162
				break
			case 5143:
				return 5163
				break
			case 5139:	//p
			case 6129:	//p
				return 5164
				break
			case 3340:	//p
				return 5165
				break
			case 5149:
				return 5169
				break
			case 5230:	//p
				return 5211
				break
			case 9111:
				return 5212
				break
			case 1314:	//p
				return 5221
				break
			case 5220:	//p
				return 5222
				break
			case 5220:	//p
				return 5223
				break
			case 4211:	//p
				return 5230
				break
			case 5210:
				return 5241
				break
			case 5220:	//p
				return 5242
				break
			case 9113:	//p
				return 5243
				break
			case 9113:	//p
				return 5244
				break
			case 5220:	//p
				return 5245
				break
			case 5220:	//p
			case 5230:	//p
				return 5246
				break
			case 5220:	//p
				return 5249
				break
			case 5131:	//p
				return 5311
				break
			case 5131:	//p
				return 5312
				break
			case 5132:	//p
				return 5321
				break
			case 5133:
				return 5322
				break
			case 5132:	//p
			case 5139:	//p
				return 5329
				break
			case 5161:
				return 5411
				break
			case 5162:
				return 5412
				break
			case 5163:
				return 5413
				break
			case 5169:	//p
			case 9152:	//p
				return 5414
				break
			case 5169:	//p
				return 5419
				break
			case 1311:	//p
			case 6111:
				return 6111
				break
			case 1311:	//p
			case 6112:
				return 6112
				break
			case 1311:	//p
			case 6113:	//p
				return 6113
				break
			case 1311:	//p
			case 6114:
				return 6114
				break
			case 1311:	//p
			case 6121:
			case 6124:	//p
				return 6121
				break
			case 1311:	//p
			case 6122:
			case 6124:	//p
				return 6122
				break
			case 6123:
			case 6124:	//p
				return 6123
				break
			case 6129:	//p
				return 6129
				break
			case 1311:	//p
			case 6130:
				return 6130
				break
			case 1311:	//p
			case 6141:
			case 6142:
				return 6210
				break
			case 1311:	//p
			case 6151:
				return 6221
				break
			case 1311:	//p
			case 6152:	//p
				return 6222
				break
			case 1311:	//p
			case 6153:
				return 6223
				break
			case 6154:
				return 6224
				break
			case 6210:	//p
				return 6310
				break
			case 6210:	//p
				return 6320
				break
			case 6210:	//p
				return 6330
				break
			case 6210:	//p
				return 6340
				break
			case 7121:
			case 7129:	//p
				return 7111
				break
			case 7122:	//p
				return 7112
				break
			case 7113:
			case 7122:	//p
				return 7113
				break
			case 7123:
				return 7114
				break
			case 7124:
				return 7115
				break
			case 7129:	//p
				return 7119
				break
			case 7131:
				return 7121
				break
			case 7132:
				return 7122
				break
			case 7133:
				return 7123
				break
			case 7134:
				return 7124
				break
			case 7135:
				return 7125
				break
			case 7136:
				return 7126
				break
			case 7233:	//p
				return 7127
				break
			case 7141:
				return 7131
				break
			case 7142:
				return 7132
				break
			case 7143:	//p
				return 7133
				break
			case 7211:
			case 8211:	//p
				return 7211
				break
			case 7212:
				return 7212
				break
			case 7213:
				return 7213
				break
			case 7214:
				return 7214
				break
			case 7215:
				return 7215
				break
			case 7221:
				return 7221
				break
			case 7222:
				return 7222
				break
			case 7223:	//p
			case 8211:	//p
				return 7223
				break
			case 7224:
				return 7224
				break
			case 7231:	//p
				return 7231
				break
			case 7232:
				return 7232
				break
			case 7233:	//p
				return 7233
				break
			case 7231:	//p
				return 7234
				break
			case 7311:	//p
				return 7311
				break
			case 7312:
				return 7312
				break
			case 7313:
				return 7313
				break
			case 7321:
				return 7314
				break
			case 7322:	//p
				return 7315
				break
			case 7323:
			case 7324:
			case 7331:	//p
			case 7424:
				return 7316
				break
			case 7332:
			case 7431:
			case 7432:	//p
				return 7318
				break
			case 7223:	//p
			case 7331:	//p
				return 7319
				break
			case 7341:	//p
			case 7342:
			case 7343:
				return 7321
				break
			case 7341:	//p
			case 7346:
			case 8251:	//p
				return 7322
				break
			case 7345:
			case 8252:	//p
				return 7323
				break
			case 7137:
				return 7411
				break
			case 7241:
				return 7412
				break
			case 7245:	//p
				return 7413
				break
			case 7242:	//p
			case 7243:	//p
				return 7421
				break
			case 7242:	//p
			case 7243:	//p
			case 7244:
			case 7245:	//p
				return 7422
				break
			case 7411:
				return 7511
				break
			case 7412:
				return 7512
				break
			case 7413:
				return 7513
				break
			case 7414:
				return 7514
				break
			case 7415:
				return 7515
				break
			case 7416:
				return 7516
				break
			case 7421:
			case 8240:	//p
				return 7521
				break
			case 7422:
				return 7522
				break
			case 7423:
			case 8240:	//p
				return 7523
				break
			case 7433:
			case 7434:
				return 7531
				break
			case 7435:
				return 7532
				break
			case 7436:
				return 7533
				break
			case 7437:
				return 7534
				break
			case 7441:
				return 7535
				break
			case 7442:
				return 7536
				break
			case 6152:	//p
			case 7216:
				return 7541
				break
			case 7112:
				return 7542
				break
			case 3152:	//p
				return 7543
				break
			case 7143:	//p
				return 7544
				break
			case 7322:	//p
				return 7549
				break
			case 7111:	//p
			case 8111:	//p
				return 8111
				break
			case 8112:
				return 8112
				break
			case 8113:
				return 8113
				break
			case 8212:
				return 8114
				break
			case 8121:	//p
			case 8122:	//p
			case 8123:	//p
			case 8124:	//p
				return 8121
				break
			case 8223:	//p
				return 8122
				break
			case 8151:
			case 8152:	//p
			case 8153:	//p
			case 8154:	//p
			case 8155:	//p
			case 8159:	//p
			case 8221:	//p
			case 8222:	//p
			case 8229:	//p
				return 8131
				break
			case 7344:
			case 8224:	//p
				return 8132
				break
			case 8231:	//p
				return 8141
				break
			case 8232:	//p
				return 8142
				break
			case 8253:	//p
				return 8143
				break
			case 8261:	//p
				return 8151
				break
			case 7432:	//p
			case 8262:	//p
				return 8152
				break
			case 8263:	//p
				return 8153
				break
			case 8264:	//p
				return 8154
				break
			case 8265:	//p
				return 8155
				break
			case 8266:	//p
				return 8156
				break
			case 8264:	//p
				return 8157
				break
			case 8269:	//p
				return 8159
				break
			case 8271:	//p
			case 8272:	//p
			case 8273:	//p
			case 8274:	//p
			case 8275:	//p
			case 8276:	//p
			case 8277:	//p
			case 8278:	//p
			case 8279:	//p
				return 8160
				break
			case 8142:	//p
			case 8143:	//p
				return 8171
				break
			case 8141:
				return 8172
				break
			case 8131:
			case 8139:
				return 8181
				break
			case 8162:
				return 8182
				break
			case 8290:	//p
				return 8183
				break
			case 8290:	//p
				return 8189
				break
			case 8281:	//p
				return 8211
				break
			case 8282:	//p
			case 8283:	//p
				return 8212
				break
			case 8284:	//p
			case 8285:	//p
			case 8286:	//p
			case 8290:	//p
				return 8219
				break
			case 8311:
				return 8311
				break
			case 8312:
				return 8312
				break
			case 8321:
				return 8321
				break
			case 8322:
				return 8322
				break
			case 8323:
				return 8331
				break
			case 8324:
				return 8332
				break
			case 8331:
				return 8341
				break
			case 8332:
				return 8342
				break
			case 8333:
				return 8343
				break
			case 8334:
				return 8344
				break
			case 8340:
				return 8350
				break
			case 9131:
				return 9111
				break
			case 9132:	//p
				return 9112
				break
			case 9133:
				return 9121
				break
			case 9142:	//p
				return 9122
				break
			case 9142:	//p
				return 9123
				break
			case 9142:	//p
				return 9129
				break
			case 9211:	//p
				return 9211
				break
			case 9211:	//p
				return 9212
				break
			case 9211:	//p
				return 9213
				break
			case 6113:	//p
			case 9211:	//p
				return 9214
				break
			case 9212:
				return 9215
				break
			case 9213:
				return 9216
				break
			case 9311:
				return 9311
				break
			case 9312:
				return 9312
				break
			case 9313:
				return 9313
				break
			case 9322:	//p
				return 9321
				break
			case 9321:	//p
			case 9322:	//p
				return 9329
				break
			case 9331:
				return 9331
				break
			case 9332:
				return 9332
				break
			case 9333:	//p
				return 9333
				break
			case 9333:	//p
				return 9334
				break
			case 5122:	//p
				return 9411
				break
			case 9132:	//p
				return 9412
				break
			case 9120:
				return 9510
				break
			case 9112:
				return 9520
				break
			case 9161:	//p
				return 9611
				break
			case 9161:	//p
			case 9321:
				return 9612
				break
			case 9162:	//p
				return 9613
				break
			case 9151:
			case 9152:	//p
				return 9621
				break
			case 9162:	//p
				return 9622
				break
			case 9153:
				return 9623
				break
			case 9162:	//p
				return 9624
				break
			case 9152:	//p
				return 9629
				break
			case 0110:	//p
				return 0110
				break
			case 0110:	//p
				return 0210
				break
			case 0110:	//p
				return 0310
				break
			case 1111:
				return 1110
				break
			case 1112:
				return 1120
				break
			case 1113:
				return 1130
				break
			case 1114:	//p
				return 1141
				break
			case 1114:	//p
				return 1142
				break
			case 1114:	//p
				return 1143
				break
			case 1120:
				return 1210
				break
			case 1311:
			case 1312:
				return 1221
				break
			case 1321:	//p
			case 1322:	//p
				return 1222
				break
			case 1323:	//p
				return 1223
				break
			case 3123:	//p
				return 1223
				break
			case 1420:	//p
				return 1224
				break
			case 1411:	//p
				return 1225
				break
			case 1412:	//p
				return 1225
				break
			case 1324:	//p
			case 1330:	//p
				return 1226
				break
			case 1219:	//p
				return 1227
				break
			case 1346:
				return 1227
				break
			case 1219:	//p
				return 1228
				break
			case 1213:	//p
			case 1219:	//p
			case 1341:	//p
			case 1342:	//p
			case 1343:	//p
			case 1344:	//p
			case 1345:	//p
			case 1349:	//p
			case 1439:	//p
			case 2654:	//p
			case 3435:	//p
				return 1229
				break
			case 1211:	//p
			case 1219:	//p
				return 1231
				break
			case 1212:	//p
				return 1232
				break
			case 1221:	//p
				return 1233
				break
			case 1222:	//p
				return 1234
				break
			case 1324:	//p
				return 1235
				break
			case 1330:	//p
				return 1236
				break
			case 1223:	//p
				return 1237
				break
			case 1213:	//p
				return 1239
				break
			case 6111:	//p
			case 6112:	//p
			case 6113:	//p
			case 6114:	//p
			case 6121:	//p
			case 6122:	//p
			case 6130:	//p
			case 6210:	//p
			case 6221:	//p
			case 6222:	//p
			case 6223:	//p
				return 1311
				break
			case 1321:	//p
			case 1322:	//p
				return 1312
				break
			case 1323:	//p
				return 1313
				break
			case 1420:	//p
			case 5221:
				return 1314
				break
			case 1411:	//p
			case 1412:	//p
				return 1315
				break
			case 1324:	//p
			case 1330:	//p
				return 1316
				break
			case 1211:	//p
			case 1212:	//p
			case 1219:	//p
			case 1221:	//p
			case 1222:	//p
			case 1330:	//p
			case 1346:
				return 1317
				break
			case 1219:	//p
				return 1318
				break
			case 1223:	//p
			case 1341:	//p
			case 1342:	//p
			case 1343:	//p
			case 1344:	//p
			case 1345:	//p
			case 1349:	//p
			case 1431:
			case 1439:	//p
				return 1319
				break
			case 2111:
				return 2111
				break
			case 2112:
				return 2112
				break
			case 2113:
			case 2262:	//p
				return 2113
				break
			case 2114:
				return 2114
				break
			case 2120:	//p
				return 2121
				break
			case 2120:	//p
				return 2122
				break
			case 2511:
			case 2512:
			case 2513:	//p
			case 2519:	//p
			case 2521:
			case 2522:
			case 2523:	//p
				return 2131
				break
			case 2513:	//p
			case 2514:
			case 2519:	//p
			case 2523:	//p
				return 2132
				break
			case 2513:	//p
			case 2519:	//p
			case 2529:
				return 2139
				break
			case 2161:
			case 2162:
			case 2164:
				return 2141
				break
			case 2142:
				return 2142
				break
			case 2151:
				return 2143
				break
			case 2152:
			case 2153:
				return 2144
				break
			case 2144:
				return 2145
				break
			case 2145:
				return 2146
				break
			case 2146:
				return 2147
				break
			case 2165:
				return 2148
				break
			case 2141:
			case 2143:
			case 2149:
				return 2149
				break
			case 2131:	//p
			case 2133:
				return 2211
				break
			case 2131:	//p
			case 2212:
			case 2250:	//p
				return 2212
				break
			case 2132:	//p
				return 2213
				break
			case 2211:
			case 2212:
				return 2221
				break
			case 2261:
				return 2222
				break
			case 2250:	//p
				return 2223
				break
			case 2262:	//p
				return 2224
				break
			case 2263:	//p
			case 2269:	//p
				return 2229
				break
			case 1342:	//p
			case 1343:	//p
			case 2221:	//p
			case 2222:	//p
			case 3221:	//p
			case 3222:	//p
				return 2230
				break
			case 2310:
			case 2320:	//p
				return 2310
				break
			case 2320:	//p
			case 2330:
				return 2320
				break
			case 2341:	//p
			case 2342:	//p
			case 2352:	//p
				return 2331
				break
			case 2351:	//p
				return 2351
				break
			case 2351:	//p
				return 2352
				break
			case 2353:	//p
			case 2354:
			case 2355:	//p
			case 2356:	//p
			case 2359:	//p
				return 2359
				break
			case 2411:
			case 2412:	//p
				return 2411
				break
			case 2263:	//p
			case 2423:
			case 2424:
				return 2412
				break
			case 2412:	//p
			case 2413:
			case 2421:
			case 2422:
			case 2431:
			case 2432:
			case 3339:	//p
				return 2419
				break
			case 2611:
				return 2421
				break
			case 2612:
				return 2422
				break
			case 2619:
				return 2429
				break
			case 2621:
				return 2431
				break
			case 2622:
				return 2432
				break
			case 2631:
				return 2441
				break
			case 2632:
				return 2442
				break
			case 2633:
				return 2443
				break
			case 2643:
				return 2444
				break
			case 2634:
				return 2445
				break
			case 2635:
				return 2446
				break
			case 2431:
			case 2432:
			case 2641:
			case 2642:	//p
				return 2451
				break
			case 2651:
			case 2166:	//p
				return 2452
				break
			case 2652:	//p
				return 2453
				break
			case 2653:	//p
				return 2454
				break
			case 2654:	//p
			case 2655:
				return 2455
				break
			case 2636:
				return 2460
				break
			case 3111:
				return 3111
				break
			case 3112:	//p
				return 3112
				break
			case 3113:	//p
				return 3113
				break
			case 3114:	//p
			case 3522:
				return 3114
				break
			case 3115:	//p
				return 3115
				break
			case 3116:
			case 3133:	//p
				return 3116
				break
			case 3117:	//p
				return 3117
				break
			case 3118:
				return 3118
				break
			case 3119:
				return 3119
				break
			case 3512:
			case 3513:
			case 3514:
				return 3121
				break
			case 3511:
				return 3122
				break
			case 3139:	//p
				return 3123
				break
			case 3431:
			case 3521:	//p
				return 3131
				break
			case 3521:	//p
			case 3522:
				return 3132
				break
			case 3211:
				return 3133
				break
			case 3151:
				return 3141
				break
			case 3152:
				return 3142
				break
			case 3153:	//p
				return 3143
				break
			case 3154:
				return 3144
				break
			case 3155:
				return 3145
				break
			case 3112:	//p
			case 3119:	//p
				return 3151
				break
			case 2263:	//p
			case 3113:	//p
			case 3114:	//p
			case 3115:	//p
			case 3117:	//p
			case 3257:	//p
			case 7543:
				return 3152
				break
			case 3141:
				return 3211
				break
			case 3212:
				return 3211
				break
			case 3142:
				return 3212
				break
			case 3143:
				return 3212
				break
			case 2132:	//p
				return 3213
				break
			case 2240:
				return 3221
				break
			case 3253:
				return 3221
				break
			case 3256:
				return 3221
				break
			case 2263:	//p
			case 3257:	//p
				return 3222
				break
			case 2265:
				return 3223
				break
			case 2267:
			case 3254:
				return 3224
				break
			case 3251:
				return 3225
				break
			case 2264:
			case 2269:	//p
			case 3214:	//p
			case 3255:
				return 3226
				break
			case 3240:
				return 3227
				break
			case 3213:
				return 3228
				break
			case 2230:	//p
			case 2266:
			case 2267:	//p
			case 2269:	//p
			case 3259:
				return 3229
				break
			case 3221:	//p
				return 3231
				break
			case 3222:	//p
				return 3232
				break
			case 2230:	//p
			case 3230:	//p
				return 3241
				break
			case 3413:	//p
				return 3242
				break
			case 2341:	//p
				return 3310
				break
			case 2342:	//p
				return 3320
				break
			case 2352:	//p
				return 3330
				break
			case 2353:	//p
			case 2355:	//p
			case 2356:	//p
			case 2359:	//p
			case 3153:	//p
			case 3423:	//p
			case 5165:
				return 3340
				break
			case 2412:	//p
			case 3311:
				return 3411
				break
			case 3321:
				return 3412
				break
			case 3334:
				return 3413
				break
			case 3339:	//p
			case 4221:	//p
				return 3414
				break
			case 2433:
			case 2434:
			case 3322:
				return 3415
				break
			case 3323:
				return 3416
				break
			case 3315:
			case 3339:	//p
				return 3417
				break
			case 3312:
				return 3419
				break
			case 3324:
				return 3421
				break
			case 3331:
				return 3422
				break
			case 3333:	//p
				return 3423
				break
			case 3339:	//p
				return 3429
				break
			case 3341:	//p
			case 3342:	//p
			case 3343:	//p
			case 3344:	//p
				return 3431
				break
			case 3411:
				return 3432
				break
			case 3313:	//p
				return 3433
				break
			case 3313:	//p
			case 3314:
				return 3434
				break
			case 3332:
			case 3341:	//p
			case 3343:	//p
			case 3359:	//p
			case 3433:	//p
				return 3439
				break
			case 3351:
				return 3441
				break
			case 3352:
				return 3442
				break
			case 3353:
				return 3443
				break
			case 3354:
				return 3444
				break
			case 3359:	//p
				return 3449
				break
			case 3355:
			case 3411:
				return 3450
				break
			case 3412:
				return 3460
				break
			case 2163:
			case 2166:	//p
			case 3432:
			case 3433:	//p
			case 3435:	//p
				return 3471
				break
			case 2642:	//p
			case 2656:
				return 3472
				break
			case 2652:	//p
			case 2653:	//p
				return 3473
				break
			case 2659:
			case 3435:	//p
				return 3474
				break
			case 3421:
			case 3422:
			case 3423:	//p
				return 3475
				break
			case 3413:	//p
				return 3480
				break
			case 3341:	//p
			case 4131:	//p
				return 4111
				break
			case 3341:	//p
			case 4131:	//p
				return 4112
				break
			case 3341:	//p
			case 4132:	//p
				return 4113
				break
			case 3341:	//p
			case 4132:	//p
				return 4114
				break
			case 3341:	//p
			case 3342:	//p
			case 3344:	//p
			case 4120:
				return 4115
				break
			case 3341:	//p
			case 4311:
			case 4313:
				return 4121
				break
			case 3341:	//p
			case 4312:	//p
				return 4122
				break
			case 3341:	//p
			case 4321:
				return 4131
				break
			case 3341:	//p
			case 4322:
				return 4132
				break
			case 3341:	//p
			case 4323:
				return 4133
				break
			case 3341:	//p
			case 4411:
			case 4415:
				return 4141
				break
			case 3341:	//p
			case 4412:
				return 4142
				break
			case 3252:
			case 3341:	//p
			case 4413:
				return 4143
				break
			case 4414:
				return 4144
				break
			case 3341:	//p
			case 4110:
			case 4227:
			case 4416:
			case 4419:
				return 4190
				break
			case 4211:	//p
			case 4212:	//p
			case 5230:
				return 4211
				break
			case 4211:	//p
				return 4212
				break
			case 4212:	//p
				return 4213
				break
			case 4213:
				return 4214
				break
			case 4214:
				return 4215
				break
			case 4221:	//p
				return 4221
				break
			case 3341:	//p
				return 4222
				break
			case 4222:
			case 4224:
			case 4225:
			case 4226:
			case 4229:
				return 4222
				break
			case 3341:	//p
			case 4223:
			case 4221:	//p
				return 4223
				break
			case 5111:
				return 5111
				break
			case 5112:
				return 5112
				break
			case 5113:
				return 5113
				break
			case 5151:
				return 5121
				break
			case 5152:
				return 5121
				break
			case 3434:
			case 5120:
			case 9411:
				return 5122
				break
			case 5131:
			case 5132:
				return 5123
				break
			case 5311:
			case 5312:
				return 5131
				break
			case 3258:
			case 5321:
			case 5329:
				return 5132
				break
			case 5322:
				return 5133
				break
			case 5164:
			case 5329:
				return 5139
				break
			case 5141:
			case 5142:
				return 5141
				break
			case 5162:
				return 5142
				break
			case 5163:
				return 5143
				break
			case 5169:
				return 5149
				break
			case 5161:	//p
				return 5151
				break
			case 5161:	//p
				return 5152
				break
			case 5411:
				return 5161
				break
			case 5412:
				return 5162
				break
			case 5413:
				return 5163
				break
			case 5414:	//p
			case 5419:
				return 5169
				break
			case 5241:
				return 5210
				break
			case 5222:
			case 5223:
			case 5242:
			case 5245:
			case 5246:	//p
			case 5249:
				return 5220
				break
			case 5211:
			case 5246:	//p
				return 5230
				break
			case 6111:	//p
				return 6111
				break
			case 6112:	//p
				return 6112
				break
			case 6113:	//p
			case 9214:
				return 6113
				break
			case 6114:	//p
				return 6114
				break
			case 6121:	//p
				return 6121
				break
			case 6122:	//p
				return 6122
				break
			case 6123:	//p
				return 6123
				break
			case 6121:	//p
			case 6122:	//p
			case 6123:	//p
				return 6124
				break
			case 5164:
			case 6129:
				return 6129
				break
			case 6130:	//p
				return 6130
				break
			case 6210:	//p
				return 6141
				break
			case 6210:	//p
				return 6142
				break
			case 6221:	//p
				return 6151
				break
			case 6222:	//p
			case 7541:	//p
				return 6152
				break
			case 6223:	//p
				return 6153
				break
			case 6224:
				return 6154
				break
			case 6310:
			case 6330:
			case 6340:
				return 6210
				break
			case 3121:
			case 8111:	//p
				return 7111
				break
			case 7542:
				return 7112
				break
			case 7113:	//p
				return 7113
				break
			case 7111:	//p
				return 7121
				break
			case 7112:
			case 7113:	//p
				return 7122
				break
			case 7114:
				return 7123
				break
			case 7115:
				return 7124
				break
			case 3123:	//p
			case 7111:	//p
			case 7119:
				return 7129
				break
			case 7121:
				return 7131
				break
			case 7122:
				return 7132
				break
			case 7123:
				return 7133
				break
			case 7124:
				return 7134
				break
			case 7125:
				return 7135
				break
			case 7126:
				return 7136
				break
			case 7411:
				return 7137
				break
			case 7131:
				return 7141
				break
			case 7132:
				return 7142
				break
			case 7133:
			case 7544:
				return 7143
				break
			case 7211:	//p
				return 7211
				break
			case 7212:
				return 7212
				break
			case 7213:
				return 7213
				break
			case 7214:
				return 7214
				break
			case 7215:
				return 7215
				break
			case 7541:	//p
				return 7216
				break
			case 7221:
				return 7221
				break
			case 7222:
				return 7222
				break
			case 7223:	//p
			case 7319:	//p
				return 7223
				break
			case 7224:
				return 7224
				break
			case 7231:
			case 7234:
				return 7231
				break
			case 7232:
				return 7232
				break
			case 7127:
			case 7233:
				return 7233
				break
			case 7412:
				return 7241
				break
			case 7421:	//p
			case 7422:	//p
				return 7242
				break
			case 7421:	//p
			case 7422:	//p
				return 7243
				break
			case 7422:	//p
				return 7244
				break
			case 7413:
			case 7422:	//p
				return 7245
				break
			case 3214:	//p
			case 7311:
				return 7311
				break
			case 7312:
				return 7312
				break
			case 7313:
				return 7313
				break
			case 7314:
				return 7321
				break
			case 7315:
			case 7549:
				return 7322
				break
			case 7316:	//p
				return 7323
				break
			case 7316:	//p
				return 7324
				break
			case 7317:	//p
			case 7319:	//p
				return 7331
				break
			case 7318:	//p
				return 7332
				break
			case 7321:	//p
			case 7322:	//p
				return 7341
				break
			case 7321:	//p
				return 7342
				break
			case 7321:	//p
				return 7343
				break
			case 8132:	//p
				return 7344
				break
			case 7323:	//p
				return 7345
				break
			case 7322:	//p
				return 7346
				break
			case 7511:
				return 7411
				break
			case 7512:
				return 7412
				break
			case 7513:
				return 7413
				break
			case 7514:
				return 7414
				break
			case 7515:
				return 7415
				break
			case 7516:
				return 7416
				break
			case 7521:	//p
				return 7421
				break
			case 7522:
				return 7422
				break
			case 7523:	//p
				return 7423
				break
			case 7317:	//p
				return 7424
				break
			case 7318:	//p
				return 7431
				break
			case 7318:	//p
			case 8152:	//p
				return 7432
				break
			case 7531:	//p
				return 7433
				break
			case 7531:	//p
				return 7434
				break
			case 7532:
				return 7435
				break
			case 7533:
				return 7436
				break
			case 7534:
				return 7437
				break
			case 7535:
				return 7441
				break
			case 7536:
				return 7442
				break
			case 3121:
			case 8111:	//p
				return 8111
				break
			case 8112:
				return 8112
				break
			case 8113:
				return 8113
				break
			case 3135:	//p
			case 8121:	//p
				return 8121
				break
			case 3135:	//p
			case 8121:	//p
				return 8122
				break
			case 3135:	//p
			case 8121:	//p
				return 8123
				break
			case 3135:	//p
			case 8121:	//p
				return 8124
				break
			case 8181:	//p
				return 8131
				break
			case 8181:	//p
				return 8139
				break
			case 8172:
				return 8141
				break
			case 3139:	//p
			case 8171:	//p
				return 8142
				break
			case 3139:	//p
			case 8171:	//p
				return 8143
				break
			case 8131:	//p
				return 8151
				break
			case 3133:	//p
			case 8131:	//p
				return 8152
				break
			case 3133:	//p
			case 8131:	//p
				return 8153
				break
			case 3133:	//p
			case 8131:	//p
				return 8154
				break
			case 3134:
			case 8131:	//p
				return 8155
				break
			case 3133:	//p
			case 8131:	//p
				return 8159
				break
			case 3131:
				return 8161
				break
			case 8182:
				return 8162
				break
			case 3132:
				return 8163
				break
			case 3122:	//p
			case 3139:	//p
				return 8171
				break
			case 3122:	//p
			case 3139:	//p
				return 8172
				break
			case 3122:	//p
			case 7211:	//p
			case 7223:	//p
				return 8211
				break
			case 8114:
				return 8212
				break
			case 3122:	//p
			case 8131:	//p
				return 8221
				break
			case 3122:	//p
			case 8131:	//p
				return 8222
				break
			case 3122:	//p
			case 8122:
				return 8223
				break
			case 3122:	//p
			case 8132:	//p
				return 8224
				break
			case 3122:	//p
			case 8131:	//p
				return 8229
				break
			case 3122:	//p
			case 8141:
				return 8231
				break
			case 3122:	//p
			case 8142:
				return 8232
				break
			case 3122:	//p
			case 7521:	//p
			case 7523:	//p
				return 8240
				break
			case 3122:	//p
			case 7322:	//p
				return 8251
				break
			case 3122:	//p
			case 7323:	//p
				return 8252
				break
			case 3122:	//p
			case 8143:
				return 8253
				break
			case 3122:	//p
			case 8151:
				return 8261
				break
			case 3122:	//p
			case 8152:	//p
				return 8262
				break
			case 3122:	//p
			case 8153:
				return 8263
				break
			case 3122:	//p
			case 8154:
			case 8157:
				return 8264
				break
			case 3122:	//p
			case 8155:
				return 8265
				break
			case 3122:	//p
			case 8156:
				return 8266
				break
			case 3122:	//p
			case 8159:
				return 8269
				break
			case 3122:	//p
			case 8160:	//p
				return 8271
				break
			case 3122:	//p
			case 8160:	//p
				return 8272
				break
			case 3122:	//p
			case 8160:	//p
				return 8273
				break
			case 3122:	//p
			case 8160:	//p
				return 8274
				break
			case 3122:	//p
			case 8160:	//p
				return 8275
				break
			case 3122:	//p
			case 8160:	//p
				return 8276
				break
			case 3122:	//p
			case 8160:	//p
				return 8277
				break
			case 3122:	//p
			case 8160:	//p
				return 8278
				break
			case 3122:	//p
			case 8160:	//p
				return 8279
				break
			case 3122:	//p
			case 8211:
				return 8281
				break
			case 3122:	//p
			case 8212:	//p
				return 8282
				break
			case 3122:	//p
			case 8212:	//p
				return 8283
				break
			case 3122:	//p
			case 8219:	//p
				return 8284
				break
			case 3122:	//p
			case 8219:	//p
				return 8285
				break
			case 3122:	//p
			case 8219:	//p
				return 8286
				break
			case 3122:	//p
			case 8183:
			case 8189:
			case 8219:	//p
				return 8290
				break
			case 8311:
				return 8311
				break
			case 8312:
				return 8312
				break
			case 8321:
				return 8321
				break
			case 8322:
				return 8322
				break
			case 8331:
				return 8323
				break
			case 8332:
				return 8324
				break
			case 8341:
				return 8331
				break
			case 8342:
				return 8332
				break
			case 8343:
				return 8333
				break
			case 8344:
				return 8334
				break
			case 8350:
				return 8340
				break
			case 5212:
				return 9111
				break
			case 9520:
				return 9112
				break
			case 5243:
			case 5244:
				return 9113
				break
			case 9510:
				return 9120
				break
			case 9111:
				return 9131
				break
			case 9112:
			case 9412:
				return 9132
				break
			case 9121:
				return 9133
				break
			case 5153:
				return 9141
				break
			case 9122:
			case 9123:
			case 9129:
				return 9142
				break
			case 9621:	//p
				return 9151
				break
			case 5414:	//p
			case 9621:	//p
			case 9629:
				return 9152
				break
			case 9623:
				return 9153
				break
			case 9611:
			case 9612:
				return 9161
				break
			case 9613:
			case 9622:
			case 9624:
				return 9162
				break
			case 9211:
			case 9212:
			case 9213:
			case 9214:
				return 9211
				break
			case 9215:
				return 9212
				break
			case 9216:
				return 9213
				break
			case 9311:
				return 9311
				break
			case 9312:
				return 9312
				break
			case 9313:
				return 9313
				break
			case 9329:	//p
				return 9321
				break
			case 9321:
			case 9329:	//p
				return 9322
				break
			case 9331:
				return 9331
				break
			case 9332:
				return 9332
				break
			case 9333:
			case 9334:
				return 9333
				break
			case 0110:
			case 0210:
			case 0310:
				return 0110
				break
			default:
				return null
				break
		}
	}
}