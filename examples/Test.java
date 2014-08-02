package io.github.adamcbrown1997.algebraicSolver.examples;

import io.github.adamcbrown1997.algebraicSolver.expression.Expression;

public class Test {
	public static void main(String[] args){
		try {
			System.out.println(new Expression("log2(16)").solve());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
