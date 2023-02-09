create table basic
(
    symbol varchar(4) not null comment '商品代号'
        primary key,
    name varchar(16) not null comment '商品名',
    type varchar(8) not null comment '分类',
    profit int not null comment '每跳毛利',
    night tinyint not null comment '是否夜盘（0=否 1=是）',
    exchange varchar(16) not null comment '所属交易所',
    amount int not null comment '合同每单位数量',
    unit varchar(4) not null comment '单位',
    relative varchar(128) null comment '相关联symbol',
    remark varchar(64) null comment 'remark',
    deleted tinyint default 0 not null,
    update_time timestamp not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '合约基本信息';

create table collect
(
    id bigint auto_increment comment '主键id'
        primary key,
    trade_date varchar(10) not null comment '交易日期',
    code varchar(10) not null comment '合约代码',
    name varchar(20) not null comment '合约名称',
    type tinyint(1) not null comment '采集类型',
    price decimal(10,2) not null comment '现价',
    position tinyint default 0 not null comment '相对位置',
    high decimal(10,2) not null comment '最高价',
    low decimal(10,2) not null comment '最低价',
    deal_vol int null comment '交易量',
    hold_vol int null comment '持仓量',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    remark varchar(64) null
)
    comment '定时数据采集分析';

create index idx_future_collect_trade_date
    on collect (trade_date);

create index idx_future_daily_collect_code_date
    on collect (code, trade_date);

create table contract
(
    symbol varchar(4) not null comment '商品代码',
    code varchar(8) not null comment '合约代码'
        primary key,
    ts_code varchar(12) not null comment 'ts code',
    main tinyint default 0 not null comment '主力=1',
    low decimal(10,2) not null comment '合约最低',
    high decimal(10,2) not null comment '合约最高',
    low_time varchar(20) null comment '新低时间',
    high_time varchar(20) null comment '新低时间',
    selected tinyint default 0 not null comment '0=否 1=是',
    create_time datetime not null comment '创建时间',
    update_time datetime not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted tinyint default 0 not null,
    constraint uidx_contract_symbol_code
        unique (symbol, code),
    constraint uidx_contract_symbol_ts_code
        unique (symbol, ts_code)
)
    comment '合约信息';

create index idx_contract_selected
    on contract (selected);

create table contract_hist
(
    symbol varchar(4) not null comment '商品代码',
    code varchar(8) not null comment '合约代码'
        primary key,
    ts_code varchar(12) not null comment 'ts code',
    main tinyint default 0 not null comment '主力=1',
    low decimal(10,2) not null comment '合约最低',
    high decimal(10,2) not null comment '合约最高',
    low_time varchar(20) null comment '新低时间',
    high_time varchar(20) null comment '新低时间',
    selected tinyint default 0 not null comment '0=否 1=是',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间',
    deleted tinyint default 0 not null,
    constraint uidx_contract_symbol_code
        unique (symbol, code),
    constraint uidx_contract_symbol_ts_code
        unique (symbol, ts_code)
)
    comment '历史合约信息';

create table gap_log
(
    ts_code varchar(20) not null,
    code varchar(16) null comment 'code',
    start_date varchar(10) null comment '产生缺口日期',
    end_date varchar(10) not null comment '缺口结束日期',
    gap_type varchar(10) not null comment '类型',
    start_price decimal(10,2) not null comment '缺口开始价格',
    cpos decimal(10,1) not null comment '合约位置',
    end_price decimal(10,2) not null comment '缺口结束价格',
    gap_rate decimal(10,2) not null comment '缺口大小%',
    is_fill tinyint default 0 not null comment '是否已回补（0=未回补，1=已回补）',
    fill_date varchar(10) null comment '回补日期',
    `check` tinyint null,
    create_time datetime null comment '插入时间',
    update_time datetime null comment '更新时间',
    constraint idx_gap_log_code_date
        unique (code, start_date)
)
    comment '缺口记录';

create table gap_log_test
(
    ts_code varchar(20) not null,
    code varchar(16) null comment 'code',
    start_date varchar(10) null comment '产生缺口日期',
    end_date varchar(10) not null comment '缺口结束日期',
    gap_type varchar(10) not null comment '类型',
    start_price decimal(10,2) not null comment '缺口开始价格',
    end_price decimal(10,2) not null comment '缺口结束价格',
    gap_rate decimal(10,2) not null comment '缺口大小%',
    is_fill tinyint default 0 not null comment '是否已回补（0=未回补，1=已回补）',
    fill_date varchar(10) null comment '回补日期',
    `check` tinyint null,
    create_time datetime null comment '插入时间',
    update_time datetime null comment '更新时间',
    constraint idx_gap_log_code_date
        unique (code, start_date)
)
    comment '缺口记录';

create table live
(
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null comment '合约code'
        primary key,
    type varchar(8) not null comment '品种',
    name varchar(16) not null comment '合约名',
    price decimal(10,2) not null comment '现价',
    `change` decimal(10,2) not null comment '涨跌幅%',
    position tinyint default 0 not null comment '相对位置%',
    bid1 decimal(10,2) null comment 'buy 1',
    ask1 decimal(10,2) null comment 'sell 1',
    open decimal(10,2) null comment '开',
    low decimal(10,2) null comment '最低',
    high decimal(10,2) null comment '最高',
    amp decimal(10,2) null comment '振幅%',
    lowest_change decimal(10,2) null comment '历史最低涨跌幅%',
    highest_change decimal(10,2) null comment '历史最高涨跌幅%',
    wave varchar(64) null comment '波动',
    update_time time not null comment '最新更新时间'
)
    comment '实时';

create index idx_future_live_trade_date_code
    on live (trade_date, code);

create table n_stat
(
    code text null,
    close_change double null,
    settle_change double null,
    up tinyint(1) null,
    days bigint null,
    `3d_change` double null,
    `5d_change` double null,
    `7d_change` double null,
    change_list text null,
    price bigint null,
    avg5d bigint null,
    avg10d bigint null,
    avg20d bigint null,
    avg60d bigint null,
    update_time datetime null
);

create table open_gap
(
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null,
    name varchar(10) not null comment 'name',
    category varchar(8) default '' not null comment 'category',
    pre_close decimal(10,2) not null comment 'pre close',
    pre_high decimal(10,2) null comment 'pre high',
    pre_low decimal(10,2) null comment 'pre low',
    open decimal(10,2) not null comment 'current open',
    open_change decimal(10,2) not null comment 'open change',
    gap_rate decimal(10,2) not null comment 'gap rate',
    day_gap tinyint null comment 'is day gap',
    contract_position tinyint not null comment 'contract relative pos',
    remark varchar(64) null comment 'remark',
    buy_low decimal(10,2) not null comment 'low price for buy',
    sell_high decimal(10,2) not null comment 'high price for sell',
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint uidx_open_gap_code_trade_date
        unique (code, trade_date)
)
    comment 'open gap log';

create index idx_open_gap_trade_date
    on open_gap (trade_date);

create table trade_daily
(
    id bigint auto_increment comment '主键id'
        primary key,
    symbol varchar(6) not null comment '商品代码',
    trade_date varchar(10) not null comment '交易日期',
    code varchar(10) null comment '合约代码',
    open decimal(10,2) null comment '开盘价',
    high decimal(10,2) null comment '最高价',
    low decimal(10,2) null comment '最低价',
    close decimal(10,2) null comment '收盘价',
    settle decimal(10,2) null comment '结算价',
    pre_close decimal(10,2) null comment '昨收盘价',
    pre_settle decimal(10,2) null comment '昨结算价',
    close_change decimal(10,2) null comment '昨收盘价涨跌幅',
    settle_change decimal(10,2) null comment '结算价涨跌幅',
    deal_vol int null comment '交易量',
    hold_vol int null comment '持仓量',
    create_time datetime not null comment '创建时间',
    constraint uidx_future_daily_trade_date_contract_code
        unique (trade_date, code)
)
    comment '每日行情';

create index idx_future_daily_code
    on trade_daily (symbol);

create index idx_future_daily_code_date
    on trade_daily (code, trade_date);

create table trade_log
(
    id bigint auto_increment comment 'id'
        primary key,
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null comment 'contract code',
    name varchar(10) not null comment '合约名称',
    type varchar(10) null comment '类型',
    factor int null comment '监控因子',
    diff decimal(10,2) null comment '监控值',
    content varchar(64) null comment '内容',
    `option` varchar(6) not null comment '操作选择',
    suggest decimal(10,2) not null comment '建议价格',
    pct_change decimal(10,2) null comment '当前涨跌幅',
    position int null comment '相对位置',
    log_time timestamp default CURRENT_TIMESTAMP not null comment '时间',
    remark varchar(32) null comment '备注'
)
    comment '行情日志';

create index idx_future_log_code_date
    on trade_log (code, trade_date);

create index idx_future_log_trade_date
    on trade_log (trade_date);

create index uidx_future_log
    on trade_log (code, type, factor, suggest, position);

create table ts_contract
(
    ts_code varchar(16) not null comment '合约代码'
        primary key,
    symbol varchar(8) not null comment '交易标识',
    exchange varchar(8) not null comment '交易所代码 CFFEX-中金所 DCE-大商所 CZCE-郑商所 SHFE-上期所 INE-上海国际能源交易中心',
    name varchar(16) null comment '中文简称',
    fut_code varchar(8) null comment '合约产品代码',
    type tinyint not null comment '合约类型（1=连续 2=主力）'
)
    comment '合约列表';

create table ts_holding
(
    trade_date varchar(10) not null comment '日期',
    symbol varchar(10) not null comment '合约代码',
    broker varchar(10) not null comment '会员简称',
    vol int default 0 not null comment '成交量',
    vol_chg int default 0 not null comment '成交量变化',
    long_hld int default 0 not null comment '持买仓量',
    long_chg int default 0 not null comment '持买仓量变化',
    short_hld int default 0 not null comment '持卖仓量',
    short_chg int default 0 not null comment '持卖仓量变化',
    constraint idx_ts_future_holding_trade_date
        unique (trade_date, symbol, broker)
)
    comment '持仓变化';

create table ts_trade_daily
(
    ts_code varchar(16) not null comment 'TS合约代码',
    trade_date varchar(10) not null comment '交易日期',
    pre_close decimal(10,2) null comment '昨收盘价',
    pre_settle decimal(10,2) null comment '昨结算价',
    open decimal(10,2) not null comment '开盘价',
    high decimal(10,2) not null comment '最高价',
    low decimal(10,2) not null comment '最低价',
    close decimal(10,2) not null comment '收盘价',
    settle decimal(10,2) not null comment '结算价',
    close_change decimal(10,2) not null comment '昨收盘价涨跌幅',
    settle_change decimal(10,2) not null comment '结算价涨跌幅',
    deal_vol int default 0 null comment '成交量(手)',
    deal_amount decimal(10,2) null comment '成交金额(万元)',
    hold_vol int default 0 null comment '持仓量(手)',
    hold_change int default 0 null comment '持仓量变化',
    create_time datetime not null comment '创建时间',
    constraint idx_ts_future_daily_code_date
        unique (ts_code, trade_date)
)
    comment '日线数据';

create table ts_trade_daily_hist
(
    ts_code varchar(16) not null comment 'TS合约代码',
    trade_date varchar(10) not null comment '交易日期',
    pre_close decimal(10,2) null comment '昨收盘价',
    pre_settle decimal(10,2) null comment '昨结算价',
    open decimal(10,2) not null comment '开盘价',
    high decimal(10,2) not null comment '最高价',
    low decimal(10,2) not null comment '最低价',
    close decimal(10,2) not null comment '收盘价',
    settle decimal(10,2) not null comment '结算价',
    close_change decimal(10,2) not null comment '昨收盘价涨跌幅',
    settle_change decimal(10,2) not null comment '结算价涨跌幅',
    deal_vol int default 0 null comment '成交量(手)',
    deal_amount decimal(10,2) null comment '成交金额(万元)',
    hold_vol int default 0 null comment '持仓量(手)',
    hold_change int default 0 null comment '持仓量变化',
    create_time datetime not null comment '创建时间',
    constraint idx_ts_future_daily_code_date
        unique (ts_code, trade_date)
)
    comment '历史日数据';

create table wave
(
    code text null,
    start text null,
    end text null,
    a double null,
    b double null,
    c double null,
    d double null,
    ap double null,
    bp double null,
    cp double null,
    dp double null,
    update_time datetime null
);

create table wave_detail
(
    code text null,
    begin text null,
    end text null,
    status text null,
    begin_price double null,
    end_price double null,
    `change` double null,
    days bigint null
);

create table week_stat
(
    code varchar(16) not null,
    week varchar(10) not null,
    `change` decimal(10,1) null,
    pct_change decimal(10,2) null,
    constraint future_week_stat_pk
        unique (code, week)
);

create definer = root@localhost function STR_SPLIT(s text, del char, i int) returns varchar(32) deterministic security invoker
-- missing source code
;
symbol	name	type	amount	unit	step	profit	exchange	night	relative	remark	deleted	update_time
A	豆一	农产	10	吨	1.00	10	DCE	1	""	""	0	2023-02-03 11:52:16
AG	白银	贵金	15	千克	1.00	15	SHF	1	黄金	""	0	2023-02-03 12:16:49
AL	铝	有色	5	吨	5.00	25	SHF	1	工业硅	""	0	2023-02-03 12:19:41
AP	苹果	农产	10	吨	1.00	10	ZCE	0	红枣	""	0	2023-02-03 11:57:35
AU	黄金	贵金	1000	克	0.02	20	SHF	1	白银	""	0	2023-02-03 12:16:49
B	豆二	农产	10	吨	0.00	0	DCE	1	豆粕	""	1	2022-08-08 10:40:20
BB	胶合板	化工	500	张	0.00	0	DCE	0		""	1	2023-02-09 10:11:45
BC	国际铜	有色	5	吨	0.00	0	INE	1		""	1	2022-03-25 15:14:15
BU	沥青	石油	10	吨	1.00	10	SHF	1	""	""	0	2023-02-03 12:21:21
C	玉米	农产	10	吨	1.00	10	DCE	1	""	""	0	2023-02-03 11:57:35
CF	棉花	农产	5	吨	5.00	25	ZCE	1	短纤	""	0	2023-02-03 11:57:35
CJ	红枣	农产	5	吨	5.00	25	ZCE	0	苹果	""	0	2023-02-03 11:52:59
CS	玉米淀粉	农产	10	吨	0.00	0	DCE	1		""	1	2022-08-08 10:40:20
CU	铜	有色	5	吨	10.00	50	SHF	1	""	""	0	2023-02-03 12:19:41
CY	棉纱	农产	10	吨	0.00	0	ZCE	1		""	1	2022-08-08 10:40:20
EB	苯乙烯	油化	5	吨	1.00	5	DCE	1	橡胶	""	0	2023-02-03 12:23:03
EG	乙二醇	煤化	10	吨	1.00	10	DCE	1	PTA、短纤	""	0	2023-02-03 12:21:21
FB	纤维板	化工	10	立方米	0.00	0	DCE	0		""	1	2022-01-29 15:14:02
FG	玻璃	化工	20	吨	1.00	20	ZCE	1	纯碱	""	0	2023-02-03 12:00:24
FU	燃料油	石油	10	吨	1.00	10	SHF	1	沥青、低硫燃油、PTA	""	0	2023-02-03 12:20:52
HC	热轧卷板	钢铁	10	吨	1.00	10	SHF	1	螺纹	""	0	2023-02-03 12:23:56
I	铁矿石	钢铁	100	吨	0.50	50	DCE	1	硅铁、锰硅、螺纹、热卷	""	0	2023-02-03 12:01:02
IC	中证500指数	金融	1	指数点	0.00	0	CFX	0		""	1	2022-01-29 16:03:39
IF	沪深300指数	金融	1	指数点	0.00	0	CFX	0		""	1	2022-01-29 16:03:39
IH	上证50股指期货	金融	300	指数点	0.00	0	CFX	0		""	1	2022-01-29 16:03:39
J	焦炭	煤炭	100	吨	0.50	50	DCE	1	黑色、PVC、尿素、聚酯	""	0	2023-02-03 12:18:03
JD	鸡蛋	农产	10	吨	1.00	10	DCE	0	""	""	0	2023-02-03 11:57:35
JM	焦煤	煤炭	60	吨	0.50	30	DCE	1	黑色、PVC、尿素、聚酯	""	0	2023-02-03 12:18:03
JR	粳稻谷	农产	10	吨	0.00	0	ZCE	1		""	1	2022-08-08 10:40:20
L	塑料	油化	5	吨	1.00	5	DCE	1	PP、PVC	""	0	2023-02-03 12:23:03
LH	生猪	农产	16	吨	5.00	80	DCE	0	""	""	0	2023-02-03 11:57:35
LR	晚籼稻	农产	10	吨	0.00	0	ZCE	1		""	1	2022-08-08 10:40:20
LU	低硫燃料油	石油	10	吨	1.00	10	INE	1	燃油、沥青	""	0	2023-02-03 12:23:03
M	豆粕	油料	10	吨	1.00	10	DCE	1	菜粕	""	0	2023-02-03 11:57:35
MA	甲醇	煤化	10	吨	1.00	10	ZCE	1	苯乙烯、乙二醇	""	0	2023-02-03 12:20:52
NI	镍	有色	1	吨	10.00	10	SHF	1	不锈钢	""	0	2023-02-03 12:19:41
NR	20号胶	化工	10	吨	5.00	50	INE	1	橡胶	""	0	2023-02-03 12:00:24
OI	菜油	油料	10	吨	1.00	10	ZCE	1	豆油、棕榈	""	0	2023-02-03 11:57:35
P	棕榈油	油料	10	吨	2.00	20	DCE	1	豆油、菜油	""	0	2023-02-03 11:57:35
PB	铅	有色	5	吨	5.00	25	SHF	1	""	""	0	2023-02-03 12:19:41
PF	短纤	油化	5	吨	2.00	10	ZCE	1	乙二醇、PTA	""	0	2023-02-03 12:23:03
PG	液化石油气	石油	20	吨	1.00	20	DCE	1	燃油、沥青	""	0	2023-02-03 12:23:03
PK	花生	农产	5	吨	2.00	10	ZCE	0	油脂	""	0	2023-02-03 11:57:35
PM	普通小麦	农产	50	吨	0.00	0	ZCE	0		""	1	2022-08-08 10:40:20
PP	聚丙烯	油化	5	吨	1.00	5	DCE	1	塑料、PVC	""	0	2023-02-03 12:23:03
RB	螺纹钢	钢铁	10	吨	1.00	10	SHF	1	热卷	""	0	2023-02-03 12:23:56
RI	早籼稻	农产	10	吨	0.00	0	ZCE	1		""	1	2022-08-08 10:40:20
RM	菜粕	油料	10	吨	1.00	10	ZCE	1	豆粕	""	0	2023-02-03 11:57:35
RR	粳米	农产	10	吨	0.00	0	DCE	1		""	1	2022-08-08 10:40:20
RS	菜籽	农产	10	吨	0.00	0	ZCE	1		""	1	2023-02-09 10:11:45
RU	天然橡胶	化工	10	吨	5.00	50	SHF	1	20号胶	""	0	2023-02-03 12:00:24
SA	纯碱	化工	20	吨	1.00	20	ZCE	1	玻璃	""	0	2023-02-03 12:00:24
SC	上海原油	石油	1000	桶	0.10	100	INE	1	燃油、沥青、LPG、PTA	""	0	2023-02-03 12:20:52
SF	硅铁	钢铁	5	吨	2.00	10	ZCE	0	锰硅	""	0	2023-02-03 12:16:49
SI	工业硅	有色	5	吨	5.00	25	GFE	0	铝	""	0	2023-02-03 12:19:41
SM	锰硅	钢铁	5	吨	2.00	10	ZCE	0	硅铁	""	0	2023-02-03 12:16:49
SN	锡	有色	1	吨	10.00	10	SHF	1	""	""	0	2023-02-03 12:19:41
SP	纸浆	化工	10	吨	2.00	20	SHF	1	""	""	0	2023-02-03 12:00:24
SR	白糖	农产	10	吨	1.00	10	ZCE	1	""	""	0	2023-02-03 11:57:35
SS	不锈钢	钢铁	5	吨	5.00	25	SHF	1	镍	""	0	2023-02-03 12:16:49
T	10年期国债	金融	1	指数点	0.00	0	CFX	0		""	1	2022-01-29 16:03:39
TA	PTA	油化	5	吨	2.00	10	ZCE	1	乙二醇、短纤	""	0	2023-02-03 12:21:21
TF	5年期国债	金融	1	指数点	0.00	0	CFX	0		""	1	2022-01-29 16:03:39
TS	2年期国债	金融	1	指数点	0.00	0	CFX	0		""	1	2022-01-29 16:03:39
UR	尿素	煤化	20	吨	1.00	20	ZCE	0	煤炭	""	0	2023-02-03 12:23:03
V	PVC	煤化	5	吨	1.00	5	DCE	1	PP、塑料	""	0	2023-02-03 12:20:52
WH	强麦	农产	10	吨	0.00	0	ZCE	1		""	1	2023-02-09 10:11:45
WR	线材	钢铁	10	吨	0.00	0	SHF	0		""	1	2022-08-05 10:13:47
Y	豆油	油料	10	吨	2.00	20	DCE	1	菜油、棕榈油	""	0	2023-02-03 11:57:35
ZC	动力煤	煤炭	100	吨	0.00	0	ZCE	1	黑色	""	1	2022-08-05 10:13:47
ZN	沪锌	有色	5	吨	5.00	25	SHF	1	""	""	0	2023-02-03 12:19:41


