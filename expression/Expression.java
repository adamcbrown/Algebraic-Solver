package io.github.adamcbrown1997.algebraicSolver.expression;

import java.util.ArrayList;

public class Expression {
	
	private enum ExpressionType{
		STRING, EXPRESSIONS, NUMBER, FUNCTION
	}
	
	private ExpressionType type;
	
	private String expression;
	
	private Expression e1;
	private char symbol;
	private Expression e2;
	
	private String function;
	
	private double numbers[];
	
	public Expression(String expression){
		type=ExpressionType.STRING;
		this.expression=expression;
	}
	
	public Expression(Expression e1, char symbol, Expression e2){
		type=ExpressionType.EXPRESSIONS;
		this.e1=e1;
		this.symbol=symbol;
		this.e2=e2;
	}
	
	public Expression(String function, Expression e1){
		type=ExpressionType.FUNCTION;
		this.function=function;
		this.e1=e1;
	}
	
	public Expression(double[] numbers){
		type=ExpressionType.NUMBER;
		this.numbers=numbers;
	}
	
	public double[] solve() throws Exception{
		switch(type){
		case STRING:
			for(int i=0;i<expression.length();i++){
				if(expression.charAt(i)=='e'&& (i==0||!EquationUtilities.isLetter(expression.charAt(i-1))) && (i==expression.length()-1||!EquationUtilities.isLetter(expression.charAt(i+1)))){
					if(i==expression.length()-1){
						expression=expression.substring(0,i)+Math.E;
					}else{
						expression=expression.substring(0,i)+Math.E+expression.substring(i+1);
					}
				}

				if((expression.charAt(i)=='p'&&expression.charAt(i+1)=='i') && (i==0||!EquationUtilities.isLetter(expression.charAt(i-1))) && (i==expression.length()-2||!EquationUtilities.isLetter(expression.charAt(i+2)))){
					if(i==expression.length()-2){
						expression=expression.substring(0,i)+Math.PI;
					}else{
						expression=expression.substring(0,i)+Math.PI+expression.substring(i+2);
					}
				}
			}
			
			for(int i=0;i<expression.length();i++){
				if(expression.charAt(i)=='-'){
					int depth=0;
					int end=-1;
					for(int subI=i+1;subI<expression.length();subI++){
						char c=expression.charAt(subI);
						if(c=='('){
							depth++;
						}
						if(c==')'){
							depth--;
							if(depth==0){
								end=subI;
								break;
							}
						}
						if(((c<'0'|| c>'9')&&c!='.')&& depth==0){
							end=subI;
							break;
						}
					}
					
					if(end==-1){
						end=expression.length();
					}
					
					if(i==0){
						return new Expression("(0-"+expression.substring(1, end)+")"+ (end==expression.length()?"":expression.substring(end))).solve();
					}
					
					char before=expression.charAt(i-1);
					if(before=='('||EquationUtilities.isOperation(before)){
						return new Expression(expression.substring(0, i)+"(0-"+expression.substring(i+1, end)+")"+ (end==expression.length()?"":expression.substring(end))).solve();
					}
				}
			}
			
			if(expression.contains("~")){
				double[] ans1=new Expression(expression.replace('~', '+')).solve();
				double[] ans2=new Expression(expression.replace('~', '-')).solve();
				
				ArrayList<Double> answers=new ArrayList<Double>();
				for(int i=0;i<ans1.length;i++){
					if(!answers.contains(ans1[i])){
						answers.add(ans1[i]);
					}
				}
				
				for(int i=0;i<ans2.length;i++){
					if(!answers.contains(ans2[i])){
						answers.add(ans2[i]);
					}
				}
				
				double[] ret=new double[answers.size()];
				for(int i=0;i<answers.size();i++){
					ret[i]=answers.get(i);
				}
				return ret;
			}
			
			if(EquationUtilities.countedCharacters(expression, '(')!=EquationUtilities.countedCharacters(expression, ')')){
				throw new Exception("Number of open parentheses does not equal the number of close parentheses");
			}
			try{
				return new double[]{Double.parseDouble(expression)};//First try to simply parse the number
			}catch(NumberFormatException e){
				if(expression.contains("(")){
					int depth=0;
					int start=0;
					for(int i=0;i<expression.length();i++){
						if(depth==0&&EquationUtilities.isLetter(expression.charAt(i))){
							int split=i-1;
							if(split==-1){
								int closeParen=-1;
								int subDepth=1;
								int openParen=expression.indexOf('(');
								
								for(int subI=openParen+1;subI<expression.length();subI++){
									if(expression.charAt(subI)=='('){
										subDepth++;
									}
									
									if(expression.charAt(subI)==')'){
										subDepth--;
										if(subDepth==0){
											closeParen=subI;
											break;
										}
									}
								}
								
								
								double[] funcValue = new Expression(expression.substring(0, openParen), new Expression(expression.substring(openParen,closeParen+1))).solve();
								if(closeParen==expression.length()-1){
									return funcValue;
								}else{
									return new Expression(new Expression(funcValue), expression.charAt(closeParen+1), new Expression(expression.substring(closeParen+2))).solve();
								}
							}else{
								return new Expression(new Expression(expression.substring(0, split)), expression.charAt(split), new Expression(expression.substring(split+1))).solve();
							}
						}
						
						if(expression.charAt(i)=='('){
							if(depth==0){
								start=i;
							}
							depth++;
						}
						
						if(expression.charAt(i)==')'){
							depth--;
							if(depth==0){
								if(start==0&&i==expression.length()-1){//Occurs in situations like (5+4), which should reduce to 5+4
									return new Expression(expression.substring(1, expression.length()-1)).solve();
								}else if(start==0){
									return new Expression(new Expression(expression.substring(0, i+1)), expression.charAt(i+1), new Expression(expression.substring(i+2))).solve();
								}else if(i==expression.length()-1){
									return new Expression(new Expression(expression.substring(0, start-1)), expression.charAt(start-1), new Expression(expression.substring(start))).solve();
								}
								
								if(EquationUtilities.isPriorityOp(expression.charAt(start-1), expression.charAt(i+1))){
									return new Expression(new Expression(expression.substring(0, i+1)), expression.charAt(i+1), new Expression(expression.substring(i+2))).solve();
								}else{
									return new Expression(new Expression(expression.substring(0, start-1)), expression.charAt(start-1), new Expression(expression.substring(start))).solve();
								}
							}
						}
					}
				}else{//If there are no parenthesis, then the order of operations should be followed
					for(char symbol : EquationUtilities.OPERATIONS){
						if(expression.contains(symbol+"")){
							int index=expression.indexOf(symbol);
							Expression e1=new Expression(expression.substring(0, index));
							Expression e2=new Expression(expression.substring(index+1));
							return new Expression(e1, symbol, e2).solve();
						}
					}
				}
			}
			break;
		case EXPRESSIONS:
			double[] e1s=e1.solve(), e2s=e2.solve();
			double[] ret=new double[e1s.length*e2s.length*(symbol=='~'?2:1)];
			for(int a=0;a<e1s.length;a++){
				for(int b=0;b<e2s.length;b++){
					switch(symbol){
					case '+':
						ret[a*e2s.length+b]=e1.solve()[a]+e2.solve()[b];
						break;
					case '-':
						ret[a*e2s.length+b]=e1.solve()[a]-e2.solve()[b];
						break;
					case '*':
						ret[a*e2s.length+b]=e1.solve()[a]*e2.solve()[b];
						break;
					case '/':
						ret[a*e2s.length+b]=e1.solve()[a]/e2.solve()[b];
						break;
					case '^':
						ret[a*e2s.length+b]=Math.pow(e1.solve()[a],e2.solve()[b]);
						break;
					}
				}
			}
			return ret;
		case FUNCTION:
			double[] solved=e1.solve();
			int length=solved.length;

			if(function.startsWith("log")){
				if(!function.equalsIgnoreCase("log")){
					length*=new Expression(function.substring(3)).solve().length;
				}
			}
			
			if(function.equalsIgnoreCase("asin")||function.equalsIgnoreCase("acos")||function.equalsIgnoreCase("atan")){
				length*=2;
			}
			
			ret=new double[length];
			
			for(int i=0;i<solved.length;i++){
				if(function.startsWith("log")){
					if(function.equalsIgnoreCase("log")){
						ret[i]=Math.log10(solved[i]);
					}else{
						Expression base=new Expression(function.substring(3));
						double[] baseSolved=base.solve();
						for(int i2=0;i2<baseSolved.length;i2++){
							ret[i+i2*solved.length]= Math.log(solved[i])/Math.log(baseSolved[i2]);
						}
					}
				}else if(function.equalsIgnoreCase("ln")){
					ret[i]= Math.log(solved[i]);
				}else if(function.equalsIgnoreCase("sin")){
					ret[i]= Math.sin(solved[i]);
				}else if(function.equalsIgnoreCase("cos")){
					ret[i]= Math.cos(solved[i]);
				}else if(function.equalsIgnoreCase("tan")){
					ret[i]= Math.tan(solved[i]);
				}else if(function.equalsIgnoreCase("asin")){
					ret[i]= Math.asin(solved[i]);
					ret[i+solved.length]=Math.PI-Math.asin(solved[i]);
				}else if(function.equalsIgnoreCase("acos")){
					ret[i]= Math.acos(solved[i]);
					ret[i+solved.length]=-Math.acos(solved[i]);
				}else if(function.equalsIgnoreCase("atan")){
					ret[i]= Math.atan(solved[i]);
					ret[i+solved.length]=Math.atan(solved[i])+Math.PI;
				}else if(function.equalsIgnoreCase("sqrt")){
					ret[i]=Math.sqrt(solved[i]);
				}
			}
			return ret;
		case NUMBER:
			return numbers;
		default:
			throw new Exception("Unsupported use of a symbol or function");
		}
		throw new Exception("UH OH");
	}
}
