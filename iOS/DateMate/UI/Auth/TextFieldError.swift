//
//  TextFieldError.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI

struct TextFieldError: View {
    let textError: String

    var body: some View {
        Text(textError)
            .foregroundColor(.red)
            .font(.caption)
            .padding(.leading, 4)
    }
}
