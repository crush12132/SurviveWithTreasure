package czz.compilation;

/**
 * 词法分析
 * @author CZZ
 * */
public class LexicalAnalysis {
	
	/**
	 * 缓冲区
	 * */
	StringBuffer buffer;
	
	/**
	 * 识别字符位置指针
	 * */
	int nump;
	
	/**
	 * 工作模式——0：准备接受新token;1：接收某个token中
	 * */
	int mode;
	
	/**
	 * 总字符数
	 * */
	int number;
	
	/**
	 * 识别token的自动机
	 * */
	FiniteStateMachine DFA;
	
	/**
	 * 识别出的token序列
	 * */
	private TokenList tklist;

	/*================================方法 methods================================*/
	
	/**
	 * 构造方法
	 * */
	public LexicalAnalysis() {		//构造方法
		this.nump = 0;
		this.mode = 0;
		this.number = 0;
	}
	
	/**
	 * 装载字符缓冲区的构造方法
	 * @param buffer 字符缓冲区
	 * */
	public LexicalAnalysis(StringBuffer buffer) {		//
		this.nump = 0;
		this.mode = 0;
		this.number = buffer.length();
		this.buffer = buffer;
		this.tklist = new TokenList();
	}
	
	/**
	 * 计算token
	 * */
	public int calculatetoken() {
		char tmpch = 0;				//当前字符
		int tmpstate = 0;				//临时状态
		if(number == 0) return 0;		//没有可以接收的内容
		while(nump < number) {	//可以继续接收
			tmpch = buffer.charAt(nump);		//接收一个字符
			if (mode == 0) {			//新的起始,构造一个自动机接收当前字符
				if(tmpch >= 'a' && tmpch <= 'z' || tmpch >= 'A' && tmpch <= 'Z') {
					DFA = new FiniteStateMachine(1,"KTiT");			//所有自动机都工作在2模式（预置ifelse）下
				}
				else if(tmpch >= '0' && tmpch <= '9') {
					DFA = new FiniteStateMachine(1, "CT");
				}
				else if(tmpch=='\'') {
					DFA = new FiniteStateMachine(1, "cT");
				}
				else if(tmpch=='\"') {
					DFA = new FiniteStateMachine(1, "sT");
				}
				else if(TokenList.strtable.contains(tmpch)) {
					DFA = new FiniteStateMachine(1, "PT");
				}
				else {
					DFA = new FiniteStateMachine(1, "others");
				}
				tmpstate = DFA.getchar(tmpch);			//状态转换
				if (DFA.name == "others") {			
					if(tmpstate == 1) mode = 0;			//可能是回车换行制表符
					else System.out.println("err ?");	//不正常
				}
				else if(DFA.name == "PT") {				//界符有可能直接接收比如(,)
					if(DFA.end_state.contains(DFA.retstate())){
						//System.out.println(DFA.buffer);
						getTklist().addtoken(DFA.name, DFA.buffer);		//装进tokenlist
						mode = 0;											//准备开始新的接收
					}
					else mode = 1;
				}
				else mode = 1;					//当前没有token，尝试继续接收
			}
			else if (mode == 1) {					//继续接收
				if(DFA.err_state.contains( (Integer)DFA.retstate() )){
					System.out.println("error!");		//是否出错
				}
				else {		//没有错误
					if(DFA.end_state.contains( (Integer)Math.abs(DFA.retstate()) )) {	//已经为结束状态
						//System.out.println(DFA.buffer);
						getTklist().addtoken(DFA.name, DFA.buffer);		//装进tokenlist
						if(tmpstate < 0) nump--;						//倒退一步（与结尾的前进一步构成原地踏步）
						mode = 0;
					}
					else {
						tmpstate = DFA.getchar(tmpch);				//接收一个字符
						if (DFA.end_state.contains( (Integer)Math.abs(DFA.retstate()) )) {	//为结束状态
							//System.out.println(DFA.buffer);
							getTklist().addtoken(DFA.name, DFA.buffer);	//装进tokenlist
							if(tmpstate < 0) nump--;					//倒退一步（与结尾的前进一步构成原地踏步）
							mode = 0;
						}
					}
				}
			}
			nump++;			//准备接收下一个字符
		}
		return 1;			//处理完成
	}

	public TokenList getTklist() {
		return tklist;
	}

	public void setTklist(TokenList tklist) {
		this.tklist = tklist;
	}
}
