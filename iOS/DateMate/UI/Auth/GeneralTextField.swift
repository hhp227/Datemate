//
//  GeneralTextField.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI

struct GeneralTextField: View {
    let label: String
    
    let value: String
    
    let onValueChange: (String) -> Void
    
    var submitLabel: SubmitLabel = .next
    
    var onSubmit: (() -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            TextField(label, text: Binding(
                get: { value },
                set: { onValueChange($0) }
            ))
            .textInputAutocapitalization(.words)
            .keyboardType(.default)
            .submitLabel(submitLabel)
            .onSubmit { onSubmit?() }
            .padding()
            .background(RoundedRectangle(cornerRadius: 12).stroke(Color.gray))
        }
    }
}
