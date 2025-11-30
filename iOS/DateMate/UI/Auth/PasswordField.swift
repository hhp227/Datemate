//
//  PasswordField.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI

struct PasswordField: View {
    @FocusState private var isFocused: Bool
    
    @State private var showPassword = false
    
    let value: String
    
    let error: String?
    
    let onValueChange: (String) -> Void
    
    var onSubmit: () -> Void = {}
    
    var isConfirmPassword: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                if showPassword {
                    TextField(
                        isConfirmPassword ? "Confirm Password" : "Password",
                        text: Binding(
                            get: { value },
                            set: { onValueChange($0) }
                        )
                    )
                    .textInputAutocapitalization(.none)
                    .autocorrectionDisabled()
                    .focused($isFocused)
                    .submitLabel(.done)
                    .onSubmit { onSubmit() }
                } else {
                    SecureField(
                        isConfirmPassword ? "Confirm Password" : "Password",
                        text: Binding(
                            get: { value },
                            set: { onValueChange($0) }
                        )
                    )
                    .focused($isFocused)
                    .submitLabel(.done)
                    .onSubmit { onSubmit() }
                }
                Button(action: { showPassword.toggle() }) {
                    Image(systemName: showPassword ? "eye.slash" : "eye")
                }
            }
            .padding()
            .background(RoundedRectangle(cornerRadius: 12).stroke(error == nil ? Color.gray : Color.red))
            if let err = error {
                TextFieldError(textError: err)
            }
        }
    }
}
