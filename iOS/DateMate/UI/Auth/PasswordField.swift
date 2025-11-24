//
//  PasswordField.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI

struct PasswordField: View {
    @State private var showPassword = false
    
    let value: String
    
    let error: String?
    
    let onValueChange: (String) -> Void
    
    var onSubmit: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                if showPassword {
                    TextField("Password", text: Binding(
                        get: { value },
                        set: { onValueChange($0) }
                    ))
                    .submitLabel(.done)
                    .onSubmit { onSubmit() }
                } else {
                    SecureField("Password", text: Binding(
                        get: { value },
                        set: { onValueChange($0) }
                    ))
                    .submitLabel(.done)
                    .onSubmit { onSubmit() }
                }
                Button(action: { showPassword.toggle() }) {
                    Image(systemName: showPassword ? "eye.slash" : "eye")
                }
            }
            .padding()
            .background(RoundedRectangle(cornerRadius: 8).stroke(error == nil ? Color.gray : Color.red))
            if let err = error {
                TextFieldError(textError: err)
            }
        }
    }
}
