package io.github.adamcbrown1997.algebraicSolver.expression;

public class Function{
	
	private String head, expression;
	private String[] parameters;
	
	public Function(String head, String expression, String[] parameters){
		this.head=head;
		this.expression=expression;
		this.parameters=new String[parameters.length];
		for(int i=0;i<parameters.length;i++){
			this.parameters[i]=parameters[i].trim();
		}
	}
	
	/**
	 * Checks if the function is syntactically valid
	 */
	public boolean isValidAsFunction(){
		String testExpression=expression;
		for(int i=0;i<testExpression.length();i++){
			for(String parameter:parameters){
				if(i+parameter.length()<=testExpression.length()){
					if(testExpression.substring(i, i+parameter.length()).equalsIgnoreCase(parameter) &&
							(i==0||!EquationUtilities.isLetter(testExpression.charAt(i-1))) &&
							(i==testExpression.length()-parameter.length()||!EquationUtilities.isLetter(testExpression.charAt(i+parameter.length())))){
						if(i==testExpression.length()-1){
							testExpression=testExpression.substring(0,i)+"0";
						}else{
							testExpression=testExpression.substring(0,i)+"0"+testExpression.substring(i+parameter.length());
						}
					}
				}
			}
		}
		try {
			new Expression(testExpression).solve();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public int getNumOfParams() {
		return parameters.length;
	}

	public String getHead() {
		return head;
	}
	
	public double[] solve(Expression[] actualParams) throws Exception{
		int possibleVariations=1;
		for(Expression e:actualParams){
			possibleVariations*=e.solve().length;
		}
		for(int i=0;i<expression.length();i++){
			for(int paramCount=0;paramCount<parameters.length;paramCount++){
				String parameter=parameters[paramCount];
				if(i+parameter.length()<=expression.length()){
					if(expression.substring(i, i+parameter.length()).equalsIgnoreCase(parameter) &&
							(i==0||!EquationUtilities.isLetter(expression.charAt(i-1))) &&
							(i==expression.length()-parameter.length()||!EquationUtilities.isLetter(expression.charAt(i+parameter.length())))){
						//Checks if a parameter is in the expression
						
						double[] paramAnswers=actualParams[paramCount].solve();
						double[][] expressionAnswers=new double[possibleVariations/paramAnswers.length][paramAnswers.length];
						int exprAnsNum=0;
						for(int paramAnsNum=0;paramAnsNum<paramAnswers.length;paramAnsNum++){
							if(i==expression.length()-1){
								expressionAnswers[exprAnsNum++]=solve(actualParams, expression.substring(0,i)+paramAnswers[paramAnsNum]);
							}else{
								expressionAnswers[exprAnsNum++]=solve(actualParams, expression.substring(0,i)+paramAnswers[paramAnsNum]+expression.substring(i+parameter.length()));
							}
						}
						
						double[] ret=new double[possibleVariations];
						for(int retNum=0;retNum<ret.length;retNum++){
							ret[retNum]=expressionAnswers[retNum/expressionAnswers[0].length][retNum%expressionAnswers[0].length];
						}
						return ret;
					}
				}
			}
		}
		return new Expression(expression).solve();
	}
	
	private double[] solve(Expression[] actualParams, String semiExpression) throws Exception{
		int possibleVariations=1;
		for(Expression e:actualParams){
			possibleVariations*=e.solve().length;
		}
		for(int i=0;i<semiExpression.length();i++){
			for(int paramCount=0;paramCount<parameters.length;paramCount++){
				String parameter=parameters[paramCount];
				if(i+parameter.length()<=semiExpression.length()){
					if(semiExpression.substring(i, i+parameter.length()).equalsIgnoreCase(parameter) &&
							(i==0||!EquationUtilities.isLetter(semiExpression.charAt(i-1))) &&
							(i==semiExpression.length()-parameter.length()||!EquationUtilities.isLetter(semiExpression.charAt(i+parameter.length())))){
						//Checks if a parameter is in the semiExpression
						
						double[] paramAnswers=actualParams[paramCount].solve();
						double[][] semiExpressionAnswers=new double[possibleVariations/paramAnswers.length][paramAnswers.length];
						int exprAnsNum=0;
						for(int paramAnsNum=0;paramAnsNum<paramAnswers.length;paramAnsNum++){
							if(i==semiExpression.length()-1){
								semiExpressionAnswers[exprAnsNum++]=solve(actualParams, semiExpression.substring(0,i)+paramAnswers[paramAnsNum]);
							}else{
								semiExpressionAnswers[exprAnsNum++]=solve(actualParams, semiExpression.substring(0,i)+paramAnswers[paramAnsNum]+semiExpression.substring(i+parameter.length()));
							}
						}
						
						double[] ret=new double[possibleVariations];
						for(int retNum=0;retNum<ret.length;retNum++){
							ret[retNum]=semiExpressionAnswers[retNum/semiExpressionAnswers[0].length][retNum%semiExpressionAnswers[0].length];
						}
						return ret;
					}
				}
			}
		}
		return new Expression(semiExpression).solve();
	}
}
