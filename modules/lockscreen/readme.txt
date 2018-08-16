项目定位
	
	用来处理充电锁模块


要求开发环境



接入步骤


   1: 在主程项中注册
                                IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                                filter.addAction(Intent.ACTION_POWER_CONNECTED);
                                filter.addAction(Intent.ACTION_SCREEN_ON);
                                mLockScreenReceiver = new LockScreenReceiver();
                                getApplicationContext().registerReceiver(mLockScreenReceiver, filter);





	






