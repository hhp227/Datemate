//
//  EmailField.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI

struct EmailField: View {
    @FocusState private var isFocused: Bool
    
    let value: String
    
    let error: String?
    
    let onValueChange: (String) -> Void
    
    var onSubmit: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            TextField("Email", text: Binding(
                get: { value },
                set: { onValueChange($0) }
            ))
            .textContentType(.emailAddress)
            .keyboardType(.emailAddress)
            .textInputAutocapitalization(.none)
            .autocorrectionDisabled()
            .focused($isFocused)
            .submitLabel(.next)
            .onSubmit { onSubmit() }
            .padding()
            .background(RoundedRectangle(cornerRadius: 12).stroke(error == nil ? Color.gray : Color.red))
            if let err = error {
                TextFieldError(textError: err)
            }
        }
    }
}
