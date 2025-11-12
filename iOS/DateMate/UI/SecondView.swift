//
//  SecondView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/12.
//

import SwiftUI

struct SecondView: View {
    var onNext: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Button("Previous") {
                onNext()
            }
            Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                .padding(.horizontal)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }
}

struct SecondView_Previews: PreviewProvider {
    static var previews: some View {
        SecondView(onNext: {})
    }
}
