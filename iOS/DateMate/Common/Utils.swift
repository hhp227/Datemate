//
//  Utils.swift
//  DateMate
//
//  Created by greenthings on 2022/12/17.
//

import Foundation

class Utils {
    // 사용자가 '010'으로 시작하게 입력했을 경우 '+82'로 자동 변환 처리
    static func formatToE164(_ number: String) -> String {
        let cleanNumber = number.trimmingCharacters(in: .whitespacesAndNewlines)

        if cleanNumber.hasPrefix("+") {
            // 1. 이미 '+'로 시작하면 그대로 반환
            return cleanNumber
        } else if cleanNumber.hasPrefix("0") {
            // 2. '0'으로 시작하면 '+82'로 변경 (첫 글자 0 제거)
            let index = cleanNumber.index(after: cleanNumber.startIndex)
            return "+82" + cleanNumber[index...]
        } else {
            // 3. 그 외의 경우 앞에 '+'만 붙여줌
            return "+" + cleanNumber
        }
    }
}
