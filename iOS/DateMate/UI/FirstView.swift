//
//  FirstView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/12.
//

import SwiftUI

struct FirstView: View {
    var onNext: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Button("Next") {
                onNext()
            }
            Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                .padding(.horizontal)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }
}

struct FirstView_Previews: PreviewProvider {
    static var previews: some View {
        FirstView(onNext: {})
    }
}
