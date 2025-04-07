package scanTask

import (
	"fmt"
	"strconv"
	"strings"
)

func check(rule string, pocResponse *POCResponse, isFinalExpression bool, resultsMap map[string]bool) bool {
	if isFinalExpression {
		return resultsMap[rule]
	}
	fmt.Printf("Checking: %s\n", rule)
	ruleStrict := strings.ReplaceAll(rule, " ", "")
	switch {
	case strings.HasPrefix(rule, "response.status"):
		parts := strings.Split(ruleStrict, "==")
		if len(parts) > 1 {
			if statusCode, err := strconv.Atoi(parts[1]); err == nil {
				if statusCode == pocResponse.Status {
					return true
				}
			} else {
				//转换数字失败的情况
				fmt.Println("未找到分隔符")
			}
		} else {
			//格式不对的情况，缺少==
			fmt.Println("未找到分隔符")
		}
	case strings.HasPrefix(rule, "response.body_string."):
		targetEnd := strings.Index(rule, "response.body_string")
		if targetEnd == -1 {
			fmt.Println("格式错误")
			break
		}
		targetEnd += len("response.body_string")
		// 在目标部分后面查找 . 的位置
		dotIndex := strings.Index(rule[targetEnd:], ".")
		if dotIndex == -1 {
			fmt.Println("格式错误")
			break
		}
		dotIndex += targetEnd // 转换为完整字符串中的位置
		// 找到第一个 ( 的位置
		parenIndex := strings.Index(rule[dotIndex:], "(")
		if parenIndex == -1 {
			fmt.Println("格式错误")
			break
		}
		parenIndex += dotIndex // 转换为完整字符串中的位置
		// 提取 . 和 ( 之间的部分
		funcName := rule[dotIndex+1 : parenIndex]
		fn := PocUtilFuncMap[funcName]
		// 找到第一个 ( 的位置
		start := strings.Index(rule, "(")
		if start == -1 {
			fmt.Println("格式错误")
			break
		}
		// 找到最后一个 ) 的位置
		end := strings.LastIndex(rule, ")")
		if end == -1 {
			fmt.Println("格式错误")
			break
		}
		// 提取括号内的内容
		content := rule[start+1 : end]
		contentTrim := strings.Trim(content, `"`)
		return fn(pocResponse, contentTrim)
	case strings.HasPrefix(rule, "response.headers["):
		fmt.Println("FTP文件协议")
	case strings.HasPrefix(rule, "ws://"):
		fmt.Println("WebSocket协议")

	}
	return false
}

// 核心解析函数：处理表达式并返回布尔值
func EvaluateRule(expr string, pocResponse *POCResponse, isFinalExpression bool, resultsMap map[string]bool) bool {
	expr = strings.TrimSpace(expr)
	if len(expr) == 0 {
		return true
	}

	// 处理括号包裹的表达式
	if expr[0] == '(' {
		endIndex := findMatchingParenthesis(expr)
		if endIndex == -1 {
			return false // 括号不匹配
		}
		subResult := EvaluateRule(expr[1:endIndex], pocResponse, isFinalExpression, resultsMap)
		remainingExpr := expr[endIndex+1:]
		return processRemainingExpression(subResult, remainingExpr, pocResponse, isFinalExpression, resultsMap)
	}

	// 查找逻辑运算符的切割点（考虑括号和字符串）
	orIndex := findOperatorIndex(expr, "||")
	if orIndex != -1 {
		return EvaluateRule(expr[:orIndex], pocResponse, isFinalExpression, resultsMap) || EvaluateRule(expr[orIndex+2:], pocResponse, isFinalExpression, resultsMap)
	}

	andIndex := findOperatorIndex(expr, "&&")
	if andIndex != -1 {
		return EvaluateRule(expr[:andIndex], pocResponse, isFinalExpression, resultsMap) && EvaluateRule(expr[andIndex+2:], pocResponse, isFinalExpression, resultsMap)
	}

	// 基础条件判断
	return check(expr, pocResponse, isFinalExpression, resultsMap)
}

// 查找匹配的右括号位置
func findMatchingParenthesis(expr string) int {
	level := 1
	inString := false
	stringChar := byte(0)

	for i := 1; i < len(expr); i++ {
		// 处理字符串内的内容
		if inString {
			if expr[i] == stringChar && expr[i-1] != '\\' {
				inString = false
			}
			continue
		}

		switch expr[i] {
		case '"', '\'': // 进入字符串模式
			inString = true
			stringChar = expr[i]
		case '(':
			level++
		case ')':
			level--
			if level == 0 {
				return i
			}
		}
	}
	return -1
}

// 查找运算符位置（考虑括号层级和字符串）
func findOperatorIndex(expr, op string) int {
	level := 0
	inString := false
	stringChar := byte(0)

	for i := 0; i < len(expr)-len(op)+1; i++ {
		// 处理字符串内的内容
		if inString {
			if expr[i] == stringChar && (i == 0 || expr[i-1] != '\\') {
				inString = false
			}
			continue
		}

		switch expr[i] {
		case '"', '\'': // 进入字符串模式
			inString = true
			stringChar = expr[i]
		case '(':
			level++
		case ')':
			level--
		}

		// 检查是否匹配运算符
		if level == 0 && strings.HasPrefix(expr[i:], op) {
			// 确保运算符前后是语法边界
			prevValid := i == 0 || isBoundary(expr[i-1])
			nextValid := i+len(op) == len(expr) || isBoundary(expr[i+len(op)])
			if prevValid && nextValid {
				return i
			}
		}
	}
	return -1
}

func isBoundary(c byte) bool {
	return c == ' ' || c == '(' || c == ')'
}

// 处理剩余表达式逻辑
func processRemainingExpression(subResult bool, remainingExpr string, pocResponse *POCResponse, isFinalExpression bool, resultsMap map[string]bool) bool {
	remainingExpr = strings.TrimSpace(remainingExpr)
	if len(remainingExpr) == 0 {
		return subResult
	}

	if strings.HasPrefix(remainingExpr, "||") {
		if subResult {
			return true
		}
		return EvaluateRule(remainingExpr[2:], pocResponse, isFinalExpression, resultsMap)
	}

	if strings.HasPrefix(remainingExpr, "&&") {
		if !subResult {
			return false
		}
		return EvaluateRule(remainingExpr[2:], pocResponse, isFinalExpression, resultsMap)
	}

	return subResult
}
