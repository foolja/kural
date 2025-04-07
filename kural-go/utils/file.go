package utils

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// ReadPasswordFile  从指定文件读取密码字典
// 参数：
//
//	filename - 要读取的文件路径
//	trimSpace - 是否去除前后空白
//	skipEmpty - 是否跳过空行
//
// 返回值：
//
//	[]string - 读取到的密码切片
//	error    - 读取过程中发生的错误
func ReadPasswordFile(filename string, trimSpace bool, skipEmpty bool) ([]string, error) {
	var passwords []string

	file, err := os.Open(filename)
	if err != nil {
		return nil, fmt.Errorf("os.Open: %w", err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()

		// 处理空白字符
		if trimSpace {
			line = strings.TrimSpace(line)
		}

		// 空行处理
		if skipEmpty && line == "" {
			continue
		}

		passwords = append(passwords, line)
	}

	if err := scanner.Err(); err != nil {
		return passwords, fmt.Errorf("scanner.Err: %w", err)
	}

	return passwords, nil
}

// ReadUsernameFile 从文件读取用户名列表，功能与密码读取保持一致
// 参数：
//
//	filename - 文件名
//	trimSpace - 去除首尾空格
//	skipEmpty - 跳过空行
//
// 返回值：
//
//	[]string - 用户名切片
//	error    - 读取错误
func ReadUsernameFile(filename string, trimSpace bool, skipEmpty bool) ([]string, error) {
	var usernames []string

	file, err := os.Open(filename)
	if err != nil {
		return nil, fmt.Errorf("os.Open: %w", err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()

		if trimSpace {
			line = strings.TrimSpace(line)
		}

		if skipEmpty && line == "" {
			continue
		}

		usernames = append(usernames, line)
	}

	if err := scanner.Err(); err != nil {
		return usernames, fmt.Errorf("scanner.Err: %w", err)
	}

	return usernames, nil
}
