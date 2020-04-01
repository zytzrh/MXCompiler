// Generated from D:/Code/Compiler/mynext2/src\MXgrammar.g4 by ANTLR 4.8
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MXgrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, Int_constant=33, Bool_constant=34, String_constant=35, INT=36, 
		BOOL=37, STRING=38, NULL=39, VOID=40, TRUE=41, FALSE=42, IF=43, ELSE=44, 
		FOR=45, WHILE=46, BREAK=47, CONTINUE=48, RETURN=49, NEW=50, CLASS=51, 
		THIS=52, ID=53, WS=54, BlockComment=55, LineComment=56;
	public static final int
		RULE_program = 0, RULE_defUnit = 1, RULE_classDef = 2, RULE_funcDef = 3, 
		RULE_varDef = 4, RULE_constructDef = 5, RULE_type = 6, RULE_nonArray = 7, 
		RULE_formalParas = 8, RULE_formalPara = 9, RULE_varDefOne = 10, RULE_block = 11, 
		RULE_statement = 12, RULE_for_init = 13, RULE_for_update = 14, RULE_expr = 15, 
		RULE_exprs = 16, RULE_newType = 17, RULE_constant = 18;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "defUnit", "classDef", "funcDef", "varDef", "constructDef", 
			"type", "nonArray", "formalParas", "formalPara", "varDefOne", "block", 
			"statement", "for_init", "for_update", "expr", "exprs", "newType", "constant"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'{'", "'}'", "'('", "')'", "','", "'['", "']'", "'='", 
			"'++'", "'--'", "'.'", "'+'", "'-'", "'!'", "'~'", "'*'", "'/'", "'%'", 
			"'<<'", "'>>'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", "'&'", 
			"'^'", "'|'", "'&&'", "'||'", null, null, null, "'int'", "'bool'", "'string'", 
			"'null'", "'void'", "'true'", "'false'", "'if'", "'else'", "'for'", "'while'", 
			"'break'", "'continue'", "'return'", "'new'", "'class'", "'this'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "Int_constant", 
			"Bool_constant", "String_constant", "INT", "BOOL", "STRING", "NULL", 
			"VOID", "TRUE", "FALSE", "IF", "ELSE", "FOR", "WHILE", "BREAK", "CONTINUE", 
			"RETURN", "NEW", "CLASS", "THIS", "ID", "WS", "BlockComment", "LineComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "MXgrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MXgrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public List<DefUnitContext> defUnit() {
			return getRuleContexts(DefUnitContext.class);
		}
		public DefUnitContext defUnit(int i) {
			return getRuleContext(DefUnitContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << STRING) | (1L << VOID) | (1L << CLASS) | (1L << ID))) != 0)) {
				{
				{
				setState(38);
				defUnit();
				}
				}
				setState(43);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefUnitContext extends ParserRuleContext {
		public ClassDefContext classDef() {
			return getRuleContext(ClassDefContext.class,0);
		}
		public FuncDefContext funcDef() {
			return getRuleContext(FuncDefContext.class,0);
		}
		public VarDefContext varDef() {
			return getRuleContext(VarDefContext.class,0);
		}
		public DefUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterDefUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitDefUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitDefUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefUnitContext defUnit() throws RecognitionException {
		DefUnitContext _localctx = new DefUnitContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_defUnit);
		try {
			setState(49);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				classDef();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(45);
				funcDef();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(46);
				varDef();
				setState(47);
				match(T__0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassDefContext extends ParserRuleContext {
		public TerminalNode CLASS() { return getToken(MXgrammarParser.CLASS, 0); }
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public List<VarDefContext> varDef() {
			return getRuleContexts(VarDefContext.class);
		}
		public VarDefContext varDef(int i) {
			return getRuleContext(VarDefContext.class,i);
		}
		public List<FuncDefContext> funcDef() {
			return getRuleContexts(FuncDefContext.class);
		}
		public FuncDefContext funcDef(int i) {
			return getRuleContext(FuncDefContext.class,i);
		}
		public List<ConstructDefContext> constructDef() {
			return getRuleContexts(ConstructDefContext.class);
		}
		public ConstructDefContext constructDef(int i) {
			return getRuleContext(ConstructDefContext.class,i);
		}
		public ClassDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterClassDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitClassDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitClassDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassDefContext classDef() throws RecognitionException {
		ClassDefContext _localctx = new ClassDefContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_classDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(CLASS);
			setState(52);
			match(ID);
			setState(53);
			match(T__1);
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << STRING) | (1L << VOID) | (1L << ID))) != 0)) {
				{
				setState(59);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
				case 1:
					{
					setState(54);
					varDef();
					setState(55);
					match(T__0);
					}
					break;
				case 2:
					{
					setState(57);
					funcDef();
					}
					break;
				case 3:
					{
					setState(58);
					constructDef();
					}
					break;
				}
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(64);
			match(T__2);
			setState(65);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncDefContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode VOID() { return getToken(MXgrammarParser.VOID, 0); }
		public FormalParasContext formalParas() {
			return getRuleContext(FormalParasContext.class,0);
		}
		public FuncDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFuncDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFuncDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFuncDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncDefContext funcDef() throws RecognitionException {
		FuncDefContext _localctx = new FuncDefContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_funcDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case BOOL:
			case STRING:
			case ID:
				{
				setState(67);
				type(0);
				}
				break;
			case VOID:
				{
				setState(68);
				match(VOID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(71);
			match(ID);
			setState(72);
			match(T__3);
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << STRING) | (1L << ID))) != 0)) {
				{
				setState(73);
				formalParas();
				}
			}

			setState(76);
			match(T__4);
			setState(77);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDefContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<VarDefOneContext> varDefOne() {
			return getRuleContexts(VarDefOneContext.class);
		}
		public VarDefOneContext varDefOne(int i) {
			return getRuleContext(VarDefOneContext.class,i);
		}
		public VarDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterVarDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitVarDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitVarDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDefContext varDef() throws RecognitionException {
		VarDefContext _localctx = new VarDefContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_varDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			type(0);
			setState(80);
			varDefOne();
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(81);
				match(T__5);
				setState(82);
				varDefOne();
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstructDefContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FormalParasContext formalParas() {
			return getRuleContext(FormalParasContext.class,0);
		}
		public ConstructDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterConstructDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitConstructDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitConstructDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstructDefContext constructDef() throws RecognitionException {
		ConstructDefContext _localctx = new ConstructDefContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_constructDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(ID);
			setState(89);
			match(T__3);
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << STRING) | (1L << ID))) != 0)) {
				{
				setState(90);
				formalParas();
				}
			}

			setState(93);
			match(T__4);
			setState(94);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ArrayTypeContext extends TypeContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ArrayTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitArrayType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitArrayType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NonArrayTypeContext extends TypeContext {
		public NonArrayContext nonArray() {
			return getRuleContext(NonArrayContext.class,0);
		}
		public NonArrayTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterNonArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitNonArrayType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitNonArrayType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new NonArrayTypeContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(97);
			nonArray();
			}
			_ctx.stop = _input.LT(-1);
			setState(104);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ArrayTypeContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(99);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(100);
					match(T__6);
					setState(101);
					match(T__7);
					}
					} 
				}
				setState(106);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class NonArrayContext extends ParserRuleContext {
		public TerminalNode BOOL() { return getToken(MXgrammarParser.BOOL, 0); }
		public TerminalNode INT() { return getToken(MXgrammarParser.INT, 0); }
		public TerminalNode STRING() { return getToken(MXgrammarParser.STRING, 0); }
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public NonArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterNonArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitNonArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitNonArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NonArrayContext nonArray() throws RecognitionException {
		NonArrayContext _localctx = new NonArrayContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_nonArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << STRING) | (1L << ID))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParasContext extends ParserRuleContext {
		public List<FormalParaContext> formalPara() {
			return getRuleContexts(FormalParaContext.class);
		}
		public FormalParaContext formalPara(int i) {
			return getRuleContext(FormalParaContext.class,i);
		}
		public FormalParasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParas; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFormalParas(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFormalParas(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFormalParas(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalParasContext formalParas() throws RecognitionException {
		FormalParasContext _localctx = new FormalParasContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_formalParas);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			formalPara();
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(110);
				match(T__5);
				setState(111);
				formalPara();
				}
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParaContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public FormalParaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalPara; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFormalPara(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFormalPara(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFormalPara(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalParaContext formalPara() throws RecognitionException {
		FormalParaContext _localctx = new FormalParaContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_formalPara);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			type(0);
			setState(118);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDefOneContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public VarDefOneContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDefOne; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterVarDefOne(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitVarDefOne(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitVarDefOne(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDefOneContext varDefOne() throws RecognitionException {
		VarDefOneContext _localctx = new VarDefOneContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_varDefOne);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			match(ID);
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(121);
				match(T__8);
				setState(122);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			match(T__1);
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__3) | (1L << T__9) | (1L << T__10) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << Int_constant) | (1L << Bool_constant) | (1L << String_constant) | (1L << INT) | (1L << BOOL) | (1L << STRING) | (1L << NULL) | (1L << IF) | (1L << FOR) | (1L << WHILE) | (1L << BREAK) | (1L << CONTINUE) | (1L << RETURN) | (1L << NEW) | (1L << THIS) | (1L << ID))) != 0)) {
				{
				{
				setState(126);
				statement();
				}
				}
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(132);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Block_stContext extends StatementContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Block_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterBlock_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitBlock_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitBlock_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Expr_stContext extends StatementContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Expr_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterExpr_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitExpr_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitExpr_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Return_stContext extends StatementContext {
		public TerminalNode RETURN() { return getToken(MXgrammarParser.RETURN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Return_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterReturn_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitReturn_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitReturn_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class If_stContext extends StatementContext {
		public ExprContext cond;
		public StatementContext then_st;
		public StatementContext else_st;
		public TerminalNode IF() { return getToken(MXgrammarParser.IF, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(MXgrammarParser.ELSE, 0); }
		public If_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterIf_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitIf_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitIf_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Empty_stContext extends StatementContext {
		public Empty_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterEmpty_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitEmpty_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitEmpty_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Continue_stContext extends StatementContext {
		public TerminalNode CONTINUE() { return getToken(MXgrammarParser.CONTINUE, 0); }
		public Continue_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterContinue_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitContinue_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitContinue_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class For_stContext extends StatementContext {
		public ExprContext cond;
		public TerminalNode FOR() { return getToken(MXgrammarParser.FOR, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public For_initContext for_init() {
			return getRuleContext(For_initContext.class,0);
		}
		public For_updateContext for_update() {
			return getRuleContext(For_updateContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public For_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFor_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFor_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFor_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class While_stContext extends StatementContext {
		public ExprContext cond;
		public TerminalNode WHILE() { return getToken(MXgrammarParser.WHILE, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public While_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterWhile_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitWhile_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitWhile_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Break_stContext extends StatementContext {
		public TerminalNode BREAK() { return getToken(MXgrammarParser.BREAK, 0); }
		public Break_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterBreak_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitBreak_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitBreak_st(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarDef_stContext extends StatementContext {
		public VarDefContext varDef() {
			return getRuleContext(VarDefContext.class,0);
		}
		public VarDef_stContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterVarDef_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitVarDef_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitVarDef_st(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_statement);
		int _la;
		try {
			setState(181);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				_localctx = new Block_stContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(134);
				block();
				}
				break;
			case 2:
				_localctx = new VarDef_stContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(135);
				varDef();
				setState(136);
				match(T__0);
				}
				break;
			case 3:
				_localctx = new If_stContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(138);
				match(IF);
				setState(139);
				match(T__3);
				setState(140);
				((If_stContext)_localctx).cond = expr(0);
				setState(141);
				match(T__4);
				setState(142);
				((If_stContext)_localctx).then_st = statement();
				setState(145);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(143);
					match(ELSE);
					setState(144);
					((If_stContext)_localctx).else_st = statement();
					}
					break;
				}
				}
				break;
			case 4:
				_localctx = new While_stContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(147);
				match(WHILE);
				setState(148);
				match(T__3);
				setState(149);
				((While_stContext)_localctx).cond = expr(0);
				setState(150);
				match(T__4);
				setState(151);
				statement();
				}
				break;
			case 5:
				_localctx = new For_stContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(153);
				match(FOR);
				setState(154);
				match(T__3);
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__9) | (1L << T__10) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << Int_constant) | (1L << Bool_constant) | (1L << String_constant) | (1L << INT) | (1L << BOOL) | (1L << STRING) | (1L << NULL) | (1L << NEW) | (1L << THIS) | (1L << ID))) != 0)) {
					{
					setState(155);
					for_init();
					}
				}

				setState(158);
				match(T__0);
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__9) | (1L << T__10) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << Int_constant) | (1L << Bool_constant) | (1L << String_constant) | (1L << NULL) | (1L << NEW) | (1L << THIS) | (1L << ID))) != 0)) {
					{
					setState(159);
					((For_stContext)_localctx).cond = expr(0);
					}
				}

				setState(162);
				match(T__0);
				setState(164);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__9) | (1L << T__10) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << Int_constant) | (1L << Bool_constant) | (1L << String_constant) | (1L << NULL) | (1L << NEW) | (1L << THIS) | (1L << ID))) != 0)) {
					{
					setState(163);
					for_update();
					}
				}

				setState(166);
				match(T__4);
				setState(167);
				statement();
				}
				break;
			case 6:
				_localctx = new Return_stContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(168);
				match(RETURN);
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__9) | (1L << T__10) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << Int_constant) | (1L << Bool_constant) | (1L << String_constant) | (1L << NULL) | (1L << NEW) | (1L << THIS) | (1L << ID))) != 0)) {
					{
					setState(169);
					expr(0);
					}
				}

				setState(172);
				match(T__0);
				}
				break;
			case 7:
				_localctx = new Break_stContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(173);
				match(BREAK);
				setState(174);
				match(T__0);
				}
				break;
			case 8:
				_localctx = new Continue_stContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(175);
				match(CONTINUE);
				setState(176);
				match(T__0);
				}
				break;
			case 9:
				_localctx = new Empty_stContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(177);
				match(T__0);
				}
				break;
			case 10:
				_localctx = new Expr_stContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(178);
				expr(0);
				setState(179);
				match(T__0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class For_initContext extends ParserRuleContext {
		public For_initContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for_init; }
	 
		public For_initContext() { }
		public void copyFrom(For_initContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class For_init_withoutDefContext extends For_initContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public For_init_withoutDefContext(For_initContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFor_init_withoutDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFor_init_withoutDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFor_init_withoutDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class For_init_withDefContext extends For_initContext {
		public VarDefContext varDef() {
			return getRuleContext(VarDefContext.class,0);
		}
		public For_init_withDefContext(For_initContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFor_init_withDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFor_init_withDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFor_init_withDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final For_initContext for_init() throws RecognitionException {
		For_initContext _localctx = new For_initContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_for_init);
		int _la;
		try {
			setState(192);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				_localctx = new For_init_withDefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(183);
				varDef();
				}
				break;
			case 2:
				_localctx = new For_init_withoutDefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(184);
				expr(0);
				setState(189);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(185);
					match(T__5);
					setState(186);
					expr(0);
					}
					}
					setState(191);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class For_updateContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public For_updateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for_update; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFor_update(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFor_update(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFor_update(this);
			else return visitor.visitChildren(this);
		}
	}

	public final For_updateContext for_update() throws RecognitionException {
		For_updateContext _localctx = new For_updateContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_for_update);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			expr(0);
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(195);
				match(T__5);
				setState(196);
				expr(0);
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FuncCall_exprContext extends ExprContext {
		public ExprContext func_name;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprsContext exprs() {
			return getRuleContext(ExprsContext.class,0);
		}
		public FuncCall_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterFuncCall_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitFuncCall_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitFuncCall_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Sub_exprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Sub_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterSub_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitSub_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitSub_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Member_exprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public Member_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterMember_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitMember_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitMember_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binary_exprContext extends ExprContext {
		public ExprContext lhs;
		public Token op;
		public ExprContext rhs;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Binary_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterBinary_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitBinary_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitBinary_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class This_exprContext extends ExprContext {
		public TerminalNode THIS() { return getToken(MXgrammarParser.THIS, 0); }
		public This_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterThis_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitThis_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitThis_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Id_exprContext extends ExprContext {
		public TerminalNode ID() { return getToken(MXgrammarParser.ID, 0); }
		public Id_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterId_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitId_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitId_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class New_exprContext extends ExprContext {
		public NewTypeContext newType() {
			return getRuleContext(NewTypeContext.class,0);
		}
		public New_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterNew_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitNew_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitNew_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Assign_exprContext extends ExprContext {
		public ExprContext lhs;
		public Token op;
		public ExprContext rhs;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Assign_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterAssign_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitAssign_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitAssign_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Postfix_exprContext extends ExprContext {
		public Token op;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Postfix_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterPostfix_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitPostfix_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitPostfix_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Prefix_exprContext extends ExprContext {
		public Token op;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Prefix_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterPrefix_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitPrefix_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitPrefix_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Subscript_exprContext extends ExprContext {
		public ExprContext array_name;
		public ExprContext index;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Subscript_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterSubscript_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitSubscript_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitSubscript_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Const_exprContext extends ExprContext {
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public Const_exprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterConst_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitConst_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitConst_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 30;
		enterRecursionRule(_localctx, 30, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case THIS:
				{
				_localctx = new This_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(203);
				match(THIS);
				}
				break;
			case Int_constant:
			case Bool_constant:
			case String_constant:
			case NULL:
				{
				_localctx = new Const_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(204);
				constant();
				}
				break;
			case ID:
				{
				_localctx = new Id_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(205);
				match(ID);
				}
				break;
			case NEW:
				{
				_localctx = new New_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(206);
				newType();
				}
				break;
			case T__9:
			case T__10:
				{
				_localctx = new Prefix_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(207);
				((Prefix_exprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__9 || _la==T__10) ) {
					((Prefix_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(208);
				expr(15);
				}
				break;
			case T__12:
			case T__13:
				{
				_localctx = new Prefix_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(209);
				((Prefix_exprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__12 || _la==T__13) ) {
					((Prefix_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(210);
				expr(14);
				}
				break;
			case T__14:
			case T__15:
				{
				_localctx = new Prefix_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(211);
				((Prefix_exprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__14 || _la==T__15) ) {
					((Prefix_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(212);
				expr(13);
				}
				break;
			case T__3:
				{
				_localctx = new Sub_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(213);
				match(T__3);
				setState(214);
				expr(0);
				setState(215);
				match(T__4);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(270);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(268);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
					case 1:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(219);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(220);
						((Binary_exprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18))) != 0)) ) {
							((Binary_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(221);
						((Binary_exprContext)_localctx).rhs = expr(13);
						}
						break;
					case 2:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(222);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(223);
						((Binary_exprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__12 || _la==T__13) ) {
							((Binary_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(224);
						((Binary_exprContext)_localctx).rhs = expr(12);
						}
						break;
					case 3:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(225);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(226);
						((Binary_exprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__19 || _la==T__20) ) {
							((Binary_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(227);
						((Binary_exprContext)_localctx).rhs = expr(11);
						}
						break;
					case 4:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(228);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(229);
						((Binary_exprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24))) != 0)) ) {
							((Binary_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(230);
						((Binary_exprContext)_localctx).rhs = expr(10);
						}
						break;
					case 5:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(231);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(232);
						((Binary_exprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__25 || _la==T__26) ) {
							((Binary_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(233);
						((Binary_exprContext)_localctx).rhs = expr(9);
						}
						break;
					case 6:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(234);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(235);
						((Binary_exprContext)_localctx).op = match(T__27);
						setState(236);
						((Binary_exprContext)_localctx).rhs = expr(8);
						}
						break;
					case 7:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(237);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(238);
						((Binary_exprContext)_localctx).op = match(T__28);
						setState(239);
						((Binary_exprContext)_localctx).rhs = expr(7);
						}
						break;
					case 8:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(240);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(241);
						((Binary_exprContext)_localctx).op = match(T__29);
						setState(242);
						((Binary_exprContext)_localctx).rhs = expr(6);
						}
						break;
					case 9:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(243);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(244);
						((Binary_exprContext)_localctx).op = match(T__30);
						setState(245);
						((Binary_exprContext)_localctx).rhs = expr(5);
						}
						break;
					case 10:
						{
						_localctx = new Binary_exprContext(new ExprContext(_parentctx, _parentState));
						((Binary_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(246);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(247);
						((Binary_exprContext)_localctx).op = match(T__31);
						setState(248);
						((Binary_exprContext)_localctx).rhs = expr(4);
						}
						break;
					case 11:
						{
						_localctx = new Assign_exprContext(new ExprContext(_parentctx, _parentState));
						((Assign_exprContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(249);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(250);
						((Assign_exprContext)_localctx).op = match(T__8);
						setState(251);
						((Assign_exprContext)_localctx).rhs = expr(2);
						}
						break;
					case 12:
						{
						_localctx = new Postfix_exprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(252);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(253);
						((Postfix_exprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__9 || _la==T__10) ) {
							((Postfix_exprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					case 13:
						{
						_localctx = new Member_exprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(254);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(255);
						match(T__11);
						setState(256);
						match(ID);
						}
						break;
					case 14:
						{
						_localctx = new FuncCall_exprContext(new ExprContext(_parentctx, _parentState));
						((FuncCall_exprContext)_localctx).func_name = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(257);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(258);
						match(T__3);
						setState(260);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__9) | (1L << T__10) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << Int_constant) | (1L << Bool_constant) | (1L << String_constant) | (1L << NULL) | (1L << NEW) | (1L << THIS) | (1L << ID))) != 0)) {
							{
							setState(259);
							exprs();
							}
						}

						setState(262);
						match(T__4);
						}
						break;
					case 15:
						{
						_localctx = new Subscript_exprContext(new ExprContext(_parentctx, _parentState));
						((Subscript_exprContext)_localctx).array_name = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(263);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(264);
						match(T__6);
						setState(265);
						((Subscript_exprContext)_localctx).index = expr(0);
						setState(266);
						match(T__7);
						}
						break;
					}
					} 
				}
				setState(272);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprsContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterExprs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitExprs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitExprs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprsContext exprs() throws RecognitionException {
		ExprsContext _localctx = new ExprsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_exprs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			expr(0);
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(274);
				match(T__5);
				setState(275);
				expr(0);
				}
				}
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NewTypeContext extends ParserRuleContext {
		public NewTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_newType; }
	 
		public NewTypeContext() { }
		public void copyFrom(NewTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Array_newTypeContext extends NewTypeContext {
		public TerminalNode NEW() { return getToken(MXgrammarParser.NEW, 0); }
		public NonArrayContext nonArray() {
			return getRuleContext(NonArrayContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Array_newTypeContext(NewTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterArray_newType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitArray_newType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitArray_newType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Normal_newTypeContext extends NewTypeContext {
		public TerminalNode NEW() { return getToken(MXgrammarParser.NEW, 0); }
		public NonArrayContext nonArray() {
			return getRuleContext(NonArrayContext.class,0);
		}
		public Normal_newTypeContext(NewTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterNormal_newType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitNormal_newType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitNormal_newType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Wrong_newTypeContext extends NewTypeContext {
		public TerminalNode NEW() { return getToken(MXgrammarParser.NEW, 0); }
		public NonArrayContext nonArray() {
			return getRuleContext(NonArrayContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Wrong_newTypeContext(NewTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterWrong_newType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitWrong_newType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitWrong_newType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NewTypeContext newType() throws RecognitionException {
		NewTypeContext _localctx = new NewTypeContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_newType);
		try {
			int _alt;
			setState(325);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				_localctx = new Wrong_newTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(281);
				match(NEW);
				setState(282);
				nonArray();
				setState(289);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(283);
						match(T__6);
						setState(284);
						expr(0);
						setState(285);
						match(T__7);
						}
						} 
					}
					setState(291);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
				}
				setState(294); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(292);
						match(T__6);
						setState(293);
						match(T__7);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(296); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(302); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(298);
						match(T__6);
						setState(299);
						expr(0);
						setState(300);
						match(T__7);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(304); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				_localctx = new Array_newTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(306);
				match(NEW);
				setState(307);
				nonArray();
				setState(312); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(308);
						match(T__6);
						setState(309);
						expr(0);
						setState(310);
						match(T__7);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(314); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(320);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(316);
						match(T__6);
						setState(317);
						match(T__7);
						}
						} 
					}
					setState(322);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
				}
				}
				break;
			case 3:
				_localctx = new Normal_newTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(323);
				match(NEW);
				setState(324);
				nonArray();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantContext extends ParserRuleContext {
		public TerminalNode Bool_constant() { return getToken(MXgrammarParser.Bool_constant, 0); }
		public TerminalNode Int_constant() { return getToken(MXgrammarParser.Int_constant, 0); }
		public TerminalNode String_constant() { return getToken(MXgrammarParser.String_constant, 0); }
		public TerminalNode NULL() { return getToken(MXgrammarParser.NULL, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MXgrammarListener ) ((MXgrammarListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MXgrammarVisitor ) return ((MXgrammarVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_constant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Int_constant) | (1L << Bool_constant) | (1L << String_constant) | (1L << NULL))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 6:
			return type_sempred((TypeContext)_localctx, predIndex);
		case 15:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 12);
		case 2:
			return precpred(_ctx, 11);
		case 3:
			return precpred(_ctx, 10);
		case 4:
			return precpred(_ctx, 9);
		case 5:
			return precpred(_ctx, 8);
		case 6:
			return precpred(_ctx, 7);
		case 7:
			return precpred(_ctx, 6);
		case 8:
			return precpred(_ctx, 5);
		case 9:
			return precpred(_ctx, 4);
		case 10:
			return precpred(_ctx, 3);
		case 11:
			return precpred(_ctx, 2);
		case 12:
			return precpred(_ctx, 20);
		case 13:
			return precpred(_ctx, 18);
		case 14:
			return precpred(_ctx, 17);
		case 15:
			return precpred(_ctx, 16);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3:\u014c\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\7\2*\n\2\f\2\16\2-\13\2\3\3\3\3\3\3\3\3\3\3\5"+
		"\3\64\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4>\n\4\f\4\16\4A\13\4\3\4"+
		"\3\4\3\4\3\5\3\5\5\5H\n\5\3\5\3\5\3\5\5\5M\n\5\3\5\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\7\6V\n\6\f\6\16\6Y\13\6\3\7\3\7\3\7\5\7^\n\7\3\7\3\7\3\7\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\7\bi\n\b\f\b\16\bl\13\b\3\t\3\t\3\n\3\n\3\n\7\ns\n\n"+
		"\f\n\16\nv\13\n\3\13\3\13\3\13\3\f\3\f\3\f\5\f~\n\f\3\r\3\r\7\r\u0082"+
		"\n\r\f\r\16\r\u0085\13\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\5\16\u0094\n\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\5\16\u009f\n\16\3\16\3\16\5\16\u00a3\n\16\3\16\3\16\5\16\u00a7"+
		"\n\16\3\16\3\16\3\16\3\16\5\16\u00ad\n\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\5\16\u00b8\n\16\3\17\3\17\3\17\3\17\7\17\u00be\n\17\f"+
		"\17\16\17\u00c1\13\17\5\17\u00c3\n\17\3\20\3\20\3\20\7\20\u00c8\n\20\f"+
		"\20\16\20\u00cb\13\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\5\21\u00dc\n\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u0107\n\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\7\21\u010f\n\21\f\21\16\21\u0112\13\21\3\22\3\22\3\22\7\22"+
		"\u0117\n\22\f\22\16\22\u011a\13\22\3\23\3\23\3\23\3\23\3\23\3\23\7\23"+
		"\u0122\n\23\f\23\16\23\u0125\13\23\3\23\3\23\6\23\u0129\n\23\r\23\16\23"+
		"\u012a\3\23\3\23\3\23\3\23\6\23\u0131\n\23\r\23\16\23\u0132\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\6\23\u013b\n\23\r\23\16\23\u013c\3\23\3\23\7\23\u0141"+
		"\n\23\f\23\16\23\u0144\13\23\3\23\3\23\5\23\u0148\n\23\3\24\3\24\3\24"+
		"\2\4\16 \25\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&\2\13\4\2&(\67"+
		"\67\3\2\f\r\3\2\17\20\3\2\21\22\3\2\23\25\3\2\26\27\3\2\30\33\3\2\34\35"+
		"\4\2#%))\2\u0176\2+\3\2\2\2\4\63\3\2\2\2\6\65\3\2\2\2\bG\3\2\2\2\nQ\3"+
		"\2\2\2\fZ\3\2\2\2\16b\3\2\2\2\20m\3\2\2\2\22o\3\2\2\2\24w\3\2\2\2\26z"+
		"\3\2\2\2\30\177\3\2\2\2\32\u00b7\3\2\2\2\34\u00c2\3\2\2\2\36\u00c4\3\2"+
		"\2\2 \u00db\3\2\2\2\"\u0113\3\2\2\2$\u0147\3\2\2\2&\u0149\3\2\2\2(*\5"+
		"\4\3\2)(\3\2\2\2*-\3\2\2\2+)\3\2\2\2+,\3\2\2\2,\3\3\2\2\2-+\3\2\2\2.\64"+
		"\5\6\4\2/\64\5\b\5\2\60\61\5\n\6\2\61\62\7\3\2\2\62\64\3\2\2\2\63.\3\2"+
		"\2\2\63/\3\2\2\2\63\60\3\2\2\2\64\5\3\2\2\2\65\66\7\65\2\2\66\67\7\67"+
		"\2\2\67?\7\4\2\289\5\n\6\29:\7\3\2\2:>\3\2\2\2;>\5\b\5\2<>\5\f\7\2=8\3"+
		"\2\2\2=;\3\2\2\2=<\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@B\3\2\2\2A?\3"+
		"\2\2\2BC\7\5\2\2CD\7\3\2\2D\7\3\2\2\2EH\5\16\b\2FH\7*\2\2GE\3\2\2\2GF"+
		"\3\2\2\2HI\3\2\2\2IJ\7\67\2\2JL\7\6\2\2KM\5\22\n\2LK\3\2\2\2LM\3\2\2\2"+
		"MN\3\2\2\2NO\7\7\2\2OP\5\30\r\2P\t\3\2\2\2QR\5\16\b\2RW\5\26\f\2ST\7\b"+
		"\2\2TV\5\26\f\2US\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2X\13\3\2\2\2YW"+
		"\3\2\2\2Z[\7\67\2\2[]\7\6\2\2\\^\5\22\n\2]\\\3\2\2\2]^\3\2\2\2^_\3\2\2"+
		"\2_`\7\7\2\2`a\5\30\r\2a\r\3\2\2\2bc\b\b\1\2cd\5\20\t\2dj\3\2\2\2ef\f"+
		"\4\2\2fg\7\t\2\2gi\7\n\2\2he\3\2\2\2il\3\2\2\2jh\3\2\2\2jk\3\2\2\2k\17"+
		"\3\2\2\2lj\3\2\2\2mn\t\2\2\2n\21\3\2\2\2ot\5\24\13\2pq\7\b\2\2qs\5\24"+
		"\13\2rp\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2u\23\3\2\2\2vt\3\2\2\2wx"+
		"\5\16\b\2xy\7\67\2\2y\25\3\2\2\2z}\7\67\2\2{|\7\13\2\2|~\5 \21\2}{\3\2"+
		"\2\2}~\3\2\2\2~\27\3\2\2\2\177\u0083\7\4\2\2\u0080\u0082\5\32\16\2\u0081"+
		"\u0080\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2"+
		"\2\2\u0084\u0086\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0087\7\5\2\2\u0087"+
		"\31\3\2\2\2\u0088\u00b8\5\30\r\2\u0089\u008a\5\n\6\2\u008a\u008b\7\3\2"+
		"\2\u008b\u00b8\3\2\2\2\u008c\u008d\7-\2\2\u008d\u008e\7\6\2\2\u008e\u008f"+
		"\5 \21\2\u008f\u0090\7\7\2\2\u0090\u0093\5\32\16\2\u0091\u0092\7.\2\2"+
		"\u0092\u0094\5\32\16\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u00b8"+
		"\3\2\2\2\u0095\u0096\7\60\2\2\u0096\u0097\7\6\2\2\u0097\u0098\5 \21\2"+
		"\u0098\u0099\7\7\2\2\u0099\u009a\5\32\16\2\u009a\u00b8\3\2\2\2\u009b\u009c"+
		"\7/\2\2\u009c\u009e\7\6\2\2\u009d\u009f\5\34\17\2\u009e\u009d\3\2\2\2"+
		"\u009e\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a2\7\3\2\2\u00a1\u00a3"+
		"\5 \21\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4"+
		"\u00a6\7\3\2\2\u00a5\u00a7\5\36\20\2\u00a6\u00a5\3\2\2\2\u00a6\u00a7\3"+
		"\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a9\7\7\2\2\u00a9\u00b8\5\32\16\2\u00aa"+
		"\u00ac\7\63\2\2\u00ab\u00ad\5 \21\2\u00ac\u00ab\3\2\2\2\u00ac\u00ad\3"+
		"\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00b8\7\3\2\2\u00af\u00b0\7\61\2\2\u00b0"+
		"\u00b8\7\3\2\2\u00b1\u00b2\7\62\2\2\u00b2\u00b8\7\3\2\2\u00b3\u00b8\7"+
		"\3\2\2\u00b4\u00b5\5 \21\2\u00b5\u00b6\7\3\2\2\u00b6\u00b8\3\2\2\2\u00b7"+
		"\u0088\3\2\2\2\u00b7\u0089\3\2\2\2\u00b7\u008c\3\2\2\2\u00b7\u0095\3\2"+
		"\2\2\u00b7\u009b\3\2\2\2\u00b7\u00aa\3\2\2\2\u00b7\u00af\3\2\2\2\u00b7"+
		"\u00b1\3\2\2\2\u00b7\u00b3\3\2\2\2\u00b7\u00b4\3\2\2\2\u00b8\33\3\2\2"+
		"\2\u00b9\u00c3\5\n\6\2\u00ba\u00bf\5 \21\2\u00bb\u00bc\7\b\2\2\u00bc\u00be"+
		"\5 \21\2\u00bd\u00bb\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf"+
		"\u00c0\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2\u00b9\3\2"+
		"\2\2\u00c2\u00ba\3\2\2\2\u00c3\35\3\2\2\2\u00c4\u00c9\5 \21\2\u00c5\u00c6"+
		"\7\b\2\2\u00c6\u00c8\5 \21\2\u00c7\u00c5\3\2\2\2\u00c8\u00cb\3\2\2\2\u00c9"+
		"\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\37\3\2\2\2\u00cb\u00c9\3\2\2"+
		"\2\u00cc\u00cd\b\21\1\2\u00cd\u00dc\7\66\2\2\u00ce\u00dc\5&\24\2\u00cf"+
		"\u00dc\7\67\2\2\u00d0\u00dc\5$\23\2\u00d1\u00d2\t\3\2\2\u00d2\u00dc\5"+
		" \21\21\u00d3\u00d4\t\4\2\2\u00d4\u00dc\5 \21\20\u00d5\u00d6\t\5\2\2\u00d6"+
		"\u00dc\5 \21\17\u00d7\u00d8\7\6\2\2\u00d8\u00d9\5 \21\2\u00d9\u00da\7"+
		"\7\2\2\u00da\u00dc\3\2\2\2\u00db\u00cc\3\2\2\2\u00db\u00ce\3\2\2\2\u00db"+
		"\u00cf\3\2\2\2\u00db\u00d0\3\2\2\2\u00db\u00d1\3\2\2\2\u00db\u00d3\3\2"+
		"\2\2\u00db\u00d5\3\2\2\2\u00db\u00d7\3\2\2\2\u00dc\u0110\3\2\2\2\u00dd"+
		"\u00de\f\16\2\2\u00de\u00df\t\6\2\2\u00df\u010f\5 \21\17\u00e0\u00e1\f"+
		"\r\2\2\u00e1\u00e2\t\4\2\2\u00e2\u010f\5 \21\16\u00e3\u00e4\f\f\2\2\u00e4"+
		"\u00e5\t\7\2\2\u00e5\u010f\5 \21\r\u00e6\u00e7\f\13\2\2\u00e7\u00e8\t"+
		"\b\2\2\u00e8\u010f\5 \21\f\u00e9\u00ea\f\n\2\2\u00ea\u00eb\t\t\2\2\u00eb"+
		"\u010f\5 \21\13\u00ec\u00ed\f\t\2\2\u00ed\u00ee\7\36\2\2\u00ee\u010f\5"+
		" \21\n\u00ef\u00f0\f\b\2\2\u00f0\u00f1\7\37\2\2\u00f1\u010f\5 \21\t\u00f2"+
		"\u00f3\f\7\2\2\u00f3\u00f4\7 \2\2\u00f4\u010f\5 \21\b\u00f5\u00f6\f\6"+
		"\2\2\u00f6\u00f7\7!\2\2\u00f7\u010f\5 \21\7\u00f8\u00f9\f\5\2\2\u00f9"+
		"\u00fa\7\"\2\2\u00fa\u010f\5 \21\6\u00fb\u00fc\f\4\2\2\u00fc\u00fd\7\13"+
		"\2\2\u00fd\u010f\5 \21\4\u00fe\u00ff\f\26\2\2\u00ff\u010f\t\3\2\2\u0100"+
		"\u0101\f\24\2\2\u0101\u0102\7\16\2\2\u0102\u010f\7\67\2\2\u0103\u0104"+
		"\f\23\2\2\u0104\u0106\7\6\2\2\u0105\u0107\5\"\22\2\u0106\u0105\3\2\2\2"+
		"\u0106\u0107\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u010f\7\7\2\2\u0109\u010a"+
		"\f\22\2\2\u010a\u010b\7\t\2\2\u010b\u010c\5 \21\2\u010c\u010d\7\n\2\2"+
		"\u010d\u010f\3\2\2\2\u010e\u00dd\3\2\2\2\u010e\u00e0\3\2\2\2\u010e\u00e3"+
		"\3\2\2\2\u010e\u00e6\3\2\2\2\u010e\u00e9\3\2\2\2\u010e\u00ec\3\2\2\2\u010e"+
		"\u00ef\3\2\2\2\u010e\u00f2\3\2\2\2\u010e\u00f5\3\2\2\2\u010e\u00f8\3\2"+
		"\2\2\u010e\u00fb\3\2\2\2\u010e\u00fe\3\2\2\2\u010e\u0100\3\2\2\2\u010e"+
		"\u0103\3\2\2\2\u010e\u0109\3\2\2\2\u010f\u0112\3\2\2\2\u0110\u010e\3\2"+
		"\2\2\u0110\u0111\3\2\2\2\u0111!\3\2\2\2\u0112\u0110\3\2\2\2\u0113\u0118"+
		"\5 \21\2\u0114\u0115\7\b\2\2\u0115\u0117\5 \21\2\u0116\u0114\3\2\2\2\u0117"+
		"\u011a\3\2\2\2\u0118\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119#\3\2\2\2"+
		"\u011a\u0118\3\2\2\2\u011b\u011c\7\64\2\2\u011c\u0123\5\20\t\2\u011d\u011e"+
		"\7\t\2\2\u011e\u011f\5 \21\2\u011f\u0120\7\n\2\2\u0120\u0122\3\2\2\2\u0121"+
		"\u011d\3\2\2\2\u0122\u0125\3\2\2\2\u0123\u0121\3\2\2\2\u0123\u0124\3\2"+
		"\2\2\u0124\u0128\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u0127\7\t\2\2\u0127"+
		"\u0129\7\n\2\2\u0128\u0126\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u0128\3\2"+
		"\2\2\u012a\u012b\3\2\2\2\u012b\u0130\3\2\2\2\u012c\u012d\7\t\2\2\u012d"+
		"\u012e\5 \21\2\u012e\u012f\7\n\2\2\u012f\u0131\3\2\2\2\u0130\u012c\3\2"+
		"\2\2\u0131\u0132\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133"+
		"\u0148\3\2\2\2\u0134\u0135\7\64\2\2\u0135\u013a\5\20\t\2\u0136\u0137\7"+
		"\t\2\2\u0137\u0138\5 \21\2\u0138\u0139\7\n\2\2\u0139\u013b\3\2\2\2\u013a"+
		"\u0136\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d\3\2"+
		"\2\2\u013d\u0142\3\2\2\2\u013e\u013f\7\t\2\2\u013f\u0141\7\n\2\2\u0140"+
		"\u013e\3\2\2\2\u0141\u0144\3\2\2\2\u0142\u0140\3\2\2\2\u0142\u0143\3\2"+
		"\2\2\u0143\u0148\3\2\2\2\u0144\u0142\3\2\2\2\u0145\u0146\7\64\2\2\u0146"+
		"\u0148\5\20\t\2\u0147\u011b\3\2\2\2\u0147\u0134\3\2\2\2\u0147\u0145\3"+
		"\2\2\2\u0148%\3\2\2\2\u0149\u014a\t\n\2\2\u014a\'\3\2\2\2\"+\63=?GLW]"+
		"jt}\u0083\u0093\u009e\u00a2\u00a6\u00ac\u00b7\u00bf\u00c2\u00c9\u00db"+
		"\u0106\u010e\u0110\u0118\u0123\u012a\u0132\u013c\u0142\u0147";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}