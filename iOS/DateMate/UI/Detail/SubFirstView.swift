//
//  SubFirstView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/12.
//

import SwiftUI

struct SubFirstView: View {
    @ObservedObject var viewModel: SubFirstViewModel

    var body: some View {
        VStack {
            Text(viewModel.data)
                .font(.title2)
                .padding()
                .onTapGesture {
                    viewModel.signOut()
                }
            Spacer()
        }
        .navigationTitle("Sub First")
    }
}

struct SubFirstView_Previews: PreviewProvider {
    static var previews: some View {
        SubFirstView(viewModel: .init(.getInstance(userRemoteDataSource: .getInstance(auth: .auth())), data: ""))
    }
}
